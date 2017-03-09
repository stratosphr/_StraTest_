package algorithms;

import algorithms.heuristics.EConcreteStateColor;
import algorithms.outputs.ApproximatedTransitionSystem;
import com.microsoft.z3.Status;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Or;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import solvers.z3.Model;
import solvers.z3.Z3;
import utilities.sets.Triplet;

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.heuristics.EConcreteStateColor.GREEN;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:36
 */
public final class UUAComputer extends AComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final ApproximatedTransitionSystem approximatedTransitionSystem;
    private final ApproximatedTransitionSystem improvedApproximatedTransitionSystem;
    private final LinkedHashMap<AbstractTransition, Boolean> MinusMarking;
    private final LinkedHashMap<AbstractTransition, Boolean> PlusMarking;
    private final Stack<Triplet<ABoolExpr, Event, AbstractState>> stack;
    private final Z3 z3;

    public UUAComputer(Machine machine, ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.machine = machine;
        this.approximatedTransitionSystem = approximatedTransitionSystem;
        this.improvedApproximatedTransitionSystem = approximatedTransitionSystem.clone();
        this.MinusMarking = new LinkedHashMap<>(approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.PlusMarking = new LinkedHashMap<>(approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPlus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.stack = new Stack<>();
        this.z3 = new Z3();
    }

    @Override
    protected ApproximatedTransitionSystem compute_() {
        MinusMarking.forEach((abstractTransition, aBoolean) -> System.out.println(abstractTransition + ": " + aBoolean));
        AbstractTransition abstractTransition;
        while (MinusMarking.values().contains(false)) {
            if ((abstractTransition = MinusMarking.keySet().stream().filter(abstractTransition1 -> !MinusMarking.get(abstractTransition1) && isMustMinusStructureEntryPoint(abstractTransition1)).findFirst().orElse(MinusMarking.keySet().stream().filter(abstractTransition1 -> !MinusMarking.get(abstractTransition1)).findFirst().orElse(null))) != null) {
                stack.push(new Triplet<>(abstractTransition.getTarget(), null, abstractTransition.getTarget()));
                mustMinusConcretization(abstractTransition, stack);
                concretizeInStack(stack);
            } else {
                throw new Error("Should never happen in UUAComputer.");
            }
        }
        return getImprovedApproximatedTransitionSystem();
    }

    private void mustMinusConcretization(AbstractTransition abstractTransition, Stack<Triplet<ABoolExpr, Event, AbstractState>> stack) {
        ABoolExpr wp = new And(abstractTransition.getSource(), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getTarget().prime());
        stack.push(new Triplet<>(wp, abstractTransition.getEvent(), abstractTransition.getSource()));
        MinusMarking.put(abstractTransition, true);
        AbstractTransition incomingMinusTransition = MinusMarking.keySet().stream().filter(abstractTransition1 -> !MinusMarking.get(abstractTransition1) && abstractTransition1.getTarget().equals(abstractTransition.getSource())).findFirst().orElse(null);
        if (incomingMinusTransition != null) {
            mustMinusConcretization(incomingMinusTransition, stack);
        }
    }

    private void concretizeInStack(Stack<Triplet<ABoolExpr, Event, AbstractState>> stack) {
        Triplet<ABoolExpr, Event, AbstractState> source = stack.pop();
        while (!stack.isEmpty()) {
            Triplet<ABoolExpr, Event, AbstractState> target = stack.pop();
            if (!concretizeFromGreenState(source, target)) {
                if (!concretizeFromBlueState(source, target)) {
                    concretizeFromAnyState(source, target);
                }
            }
            source = target;
        }
    }

    private boolean concretizeFromGreenState(Triplet<ABoolExpr, Event, AbstractState> source, Triplet<ABoolExpr, Event, AbstractState> target) {
        LinkedHashSet<ConcreteState> GCS = approximatedTransitionSystem.getConcreteTransitionSystem().getC().stream().filter(concreteState -> approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().get(concreteState).equals(GREEN)).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!GCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), getMachine().getInvariant().prime().prime(), source.getFirst(), new Or(GCS.stream().toArray(ABoolExpr[]::new)), source.getSecond().getSubstitution().getPrd(getMachine()), target.getFirst().prime()));
            if (z3.checkSAT() == Status.SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + source.getThird().getName(), model.getSource());
                ConcreteState c_ = new ConcreteState("c_" + target.getThird().getName(), model.getTarget());
                System.out.println(new ConcreteTransition(c, source.getSecond(), c_));
                //TODO: check if c is an initial state and add it to C0
                getImprovedApproximatedTransitionSystem().getConcreteTransitionSystem().getC().addAll(Arrays.asList(c, c_));
                getImprovedApproximatedTransitionSystem().getConcreteTransitionSystem().getAlpha().put(c, source.getThird());
                getImprovedApproximatedTransitionSystem().getConcreteTransitionSystem().getAlpha().put(c_, target.getThird());
                getImprovedApproximatedTransitionSystem().getConcreteTransitionSystem().getKappa().put(c, GREEN);
                getImprovedApproximatedTransitionSystem().getConcreteTransitionSystem().getKappa().put(c_, GREEN);
                getImprovedApproximatedTransitionSystem().getConcreteTransitionSystem().getDeltaC().add(new ConcreteTransition(c, source.getSecond(), c_));
                return true;
            }
        }
        return false;
    }

    private boolean concretizeFromBlueState(Triplet<ABoolExpr, Event, AbstractState> source, Triplet<ABoolExpr, Event, AbstractState> target) {
        LinkedHashSet<ConcreteState> BCS = approximatedTransitionSystem.getConcreteTransitionSystem().getC().stream().filter(concreteState -> approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().get(concreteState).equals(EConcreteStateColor.BLUE)).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!BCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), getMachine().getInvariant().prime().prime(), source.getFirst(), new Or(BCS.stream().toArray(ABoolExpr[]::new)), source.getSecond().getSubstitution().getPrd(getMachine()), target.getFirst().prime()));
            if (z3.checkSAT() == Status.SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + source.getThird().getName(), model.getSource());
                ConcreteState c_ = new ConcreteState("c_" + source.getThird().getName(), model.getTarget());
                System.out.println(new ConcreteTransition(c, source.getSecond(), c_));
                return true;
            }
        }
        return false;
    }

    private void concretizeFromAnyState(Triplet<ABoolExpr, Event, AbstractState> source, Triplet<ABoolExpr, Event, AbstractState> target) {
        z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), getMachine().getInvariant().prime().prime(), source.getFirst(), source.getSecond().getSubstitution().getPrd(getMachine()), target.getFirst().prime()));
        if (z3.checkSAT() == Status.SATISFIABLE) {
            Model model = z3.getModel();
            ConcreteState c = new ConcreteState("c_" + source.getThird(), model.getSource());
            ConcreteState c_ = new ConcreteState("c_" + target.getThird(), model.getTarget());
            System.out.println(new ConcreteTransition(c, source.getSecond(), c_));
        } else {
            throw new Error("Unable to concretize Must- transition. This should never happen.");
        }
    }

    private boolean isMustMinusStructureEntryPoint(AbstractTransition abstractTransition) {
        return MinusMarking.keySet().stream().noneMatch(abstractTransition1 -> !MinusMarking.get(abstractTransition1) && abstractTransition1.getSource().equals(abstractTransition.getTarget()));
    }

    private ApproximatedTransitionSystem getApproximatedTransitionSystem() {
        return approximatedTransitionSystem;
    }

    public ApproximatedTransitionSystem getImprovedApproximatedTransitionSystem() {
        return improvedApproximatedTransitionSystem;
    }

    private Machine getMachine() {
        return machine;
    }

}
