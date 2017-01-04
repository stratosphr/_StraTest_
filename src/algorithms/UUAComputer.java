package algorithms;

import algorithms.heuristics.EConcreteStateColor;
import algorithms.outputs.ApproximatedTransitionSystem;
import com.microsoft.z3.Status;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Or;
import eventb.graphs.AbstractTransition;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import solvers.z3.Model;
import solvers.z3.Z3;
import utilities.sets.Triple;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:36
 */
public final class UUAComputer implements IComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final ApproximatedTransitionSystem approximatedTransitionSystem;
    private final LinkedHashMap<AbstractTransition, Boolean> MinusMarking;
    private final LinkedHashMap<AbstractTransition, Boolean> PlusMarking;
    private final Stack<Triple<ABoolExpr, Event, ABoolExpr>> stack;
    private final Z3 z3;

    public UUAComputer(Machine machine, ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.machine = machine;
        this.approximatedTransitionSystem = approximatedTransitionSystem;
        this.MinusMarking = new LinkedHashMap<>(approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.PlusMarking = new LinkedHashMap<>(approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPlus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.stack = new Stack<>();
        this.z3 = new Z3();
    }

    @Override
    public ApproximatedTransitionSystem compute() {
        MinusMarking.forEach((abstractTransition, aBoolean) -> System.out.println(abstractTransition + ": " + aBoolean));
        AbstractTransition abstractTransition;
        while (MinusMarking.values().contains(false)) {
            if ((abstractTransition = MinusMarking.keySet().stream().filter(abstractTransition1 -> !MinusMarking.get(abstractTransition1) && isMustMinusStructureEntryPoint(abstractTransition1)).findFirst().orElse(null)) != null) {
                stack.push(new Triple<>(abstractTransition.getTarget(), abstractTransition.getEvent(), abstractTransition.getTarget()));
                mustMinusConcretization(abstractTransition, stack);
                concretizeInStack(stack);
            }
            if ((abstractTransition = MinusMarking.keySet().stream().filter(abstractTransition1 -> !MinusMarking.get(abstractTransition1)).findFirst().orElse(null)) != null) {
                stack.push(new Triple<>(abstractTransition.getTarget(), abstractTransition.getEvent(), abstractTransition.getTarget()));
                mustMinusConcretization(abstractTransition, stack);
                concretizeInStack(stack);
            }
        }
        return null;
    }

    private void concretizeInStack(Stack<Triple<ABoolExpr, Event, ABoolExpr>> stack) {
        LinkedHashSet<ConcreteState> GCS;
        LinkedHashSet<ConcreteState> BCS;
        ConcreteState c;
        ConcreteState c_;
        Triple<ABoolExpr, Event, ABoolExpr> source = stack.pop();
        while (!stack.isEmpty()) {
            Triple<ABoolExpr, Event, ABoolExpr> target = stack.pop();
            GCS = approximatedTransitionSystem.getConcreteTransitionSystem().getC().stream().filter(concreteState -> approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().get(concreteState).equals(EConcreteStateColor.GREEN)).collect(Collectors.toCollection(LinkedHashSet::new));
            BCS = approximatedTransitionSystem.getConcreteTransitionSystem().getC().stream().filter(concreteState -> approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().get(concreteState).equals(EConcreteStateColor.BLUE)).collect(Collectors.toCollection(LinkedHashSet::new));
            if (!GCS.isEmpty()) {
                z3.setCode(new And(
                        getMachine().getInvariant(),
                        getMachine().getInvariant().prime(),
                        getMachine().getInvariant().prime().prime(),
                        source.getFirst(),
                        new Or(GCS.stream().toArray(ABoolExpr[]::new)),
                        source.getSecond().getSubstitution().getPrd(getMachine()),
                        target.getFirst().prime()
                ));
            } else if (!BCS.isEmpty()) {
                z3.setCode(new And(
                        getMachine().getInvariant(),
                        getMachine().getInvariant().prime(),
                        getMachine().getInvariant().prime().prime(),
                        source.getFirst(),
                        new Or(BCS.stream().toArray(ABoolExpr[]::new)),
                        source.getSecond().getSubstitution().getPrd(getMachine()),
                        target.getFirst().prime()
                ));
            } else {
                throw new Error("Unhandled case in UUAComputer!");
            }
            if (z3.checkSAT() == Status.SATISFIABLE) {
                Model model = z3.getModel();
                c = new ConcreteState("c_", model.getSource());
                c_ = new ConcreteState("c_", model.getTarget());
                System.out.println(new ConcreteTransition(c, source.getSecond(), c_));
            } else {
                throw new Error("Unhandled case in UUAComputer");
            }
            source = target;
        }
    }

    private void mustMinusConcretization(AbstractTransition abstractTransition, Stack<Triple<ABoolExpr, Event, ABoolExpr>> stack) {
        ABoolExpr wp = new And(
                abstractTransition.getSource(),
                abstractTransition.getEvent().getSubstitution().getPrd(getMachine()),
                abstractTransition.getTarget().prime()
        );
        stack.push(new Triple<>(wp, abstractTransition.getEvent(), abstractTransition.getSource()));
        MinusMarking.put(abstractTransition, true);
        AbstractTransition incomingMinusTransition = MinusMarking.keySet().stream().filter(abstractTransition1 -> !MinusMarking.get(abstractTransition1) && abstractTransition1.getTarget().equals(abstractTransition.getSource())).findFirst().orElse(null);
        if (incomingMinusTransition != null) {
            mustMinusConcretization(incomingMinusTransition, stack);
        }
    }

    private boolean isMustMinusStructureEntryPoint(AbstractTransition abstractTransition) {
        return getApproximatedTransitionSystem().getTriModalTransitionSystem().getDeltaMinus().stream().noneMatch(abstractTransition2 -> abstractTransition.getTarget().equals(abstractTransition2.getSource()) && !abstractTransition2.equals(abstractTransition));
    }

    private ApproximatedTransitionSystem getApproximatedTransitionSystem() {
        return approximatedTransitionSystem;
    }

    private Machine getMachine() {
        return machine;
    }

}
