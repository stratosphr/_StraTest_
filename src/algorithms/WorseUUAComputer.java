package algorithms;

import algorithms.heuristics.EConcreteStateColor;
import algorithms.outputs.ApproximatedTransitionSystem;
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
import utilities.sets.Tuple;

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.heuristics.EConcreteStateColor.BLUE;
import static algorithms.heuristics.EConcreteStateColor.GREEN;
import static com.microsoft.z3.Status.SATISFIABLE;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:36
 */
public final class WorseUUAComputer extends AComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final ApproximatedTransitionSystem approximatedTransitionSystem;
    private final ApproximatedTransitionSystem improvedApproximatedTransitionSystem;
    private final LinkedHashMap<AbstractTransition, Boolean> MinusMarking;
    private final LinkedHashMap<AbstractTransition, Boolean> PlusMarking;
    private final Stack<Triplet<ABoolExpr, Event, AbstractState>> stack;
    private final Z3 z3;

    public WorseUUAComputer(Machine machine, ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.machine = machine;
        this.approximatedTransitionSystem = approximatedTransitionSystem;
        this.improvedApproximatedTransitionSystem = approximatedTransitionSystem.clone();
        this.MinusMarking = new LinkedHashMap<>(improvedApproximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.PlusMarking = new LinkedHashMap<>(improvedApproximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPlus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.stack = new Stack<>();
        this.z3 = new Z3();
    }

    @Override
    protected ApproximatedTransitionSystem compute_() {
        LinkedHashSet<AbstractTransition> DeltaTMinus = new LinkedHashSet<>();
        improvedApproximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus().forEach(t -> {
            if (!MinusMarking.get(t) && improvedApproximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus().stream().noneMatch(abstractTransition -> abstractTransition.getSource().equals(t.getTarget()))) {
                DeltaTMinus.add(t);
            }
        });
        DeltaTMinus.forEach(this::mustMinusConcretization);
        /*System.err.println("Here now");
        System.exit(64);*/
        improvedApproximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus().forEach(t -> {
            if (!MinusMarking.get(t)) {
                mustMinusConcretization(t);
            }
        });
        return getImprovedApproximatedTransitionSystem();
    }

    @SuppressWarnings("unchecked")
    private void mustMinusConcretization(AbstractTransition t) {
        AbstractState q = t.getSource();
        Event e = t.getEvent();
        AbstractState q_ = t.getTarget();
        Stack<Triplet<Event, ABoolExpr, AbstractState>> p = new Stack<>();
        Stack<Tuple<LinkedHashSet<AbstractTransition>, Stack<Triplet<Event, ABoolExpr, AbstractState>>>> pb = new Stack<>();
        p.push(new Triplet<>(e, q_, q_));
        LinkedHashSet<AbstractTransition> DeltaTMinus = new LinkedHashSet<>();
        DeltaTMinus.add(t);
        ABoolExpr wp_q_ = new And(machine.getInvariant(), q_);
        MinusMarking.put(t, true);
        while (!DeltaTMinus.isEmpty()) {
            while (!DeltaTMinus.isEmpty()) {
                wp_q_ = new And(
                        machine.getInvariant(),
                        machine.getInvariant().prime(),
                        q,
                        e.getSubstitution().getPrd(machine),
                        wp_q_.prime()
                );
                DeltaTMinus.clear();
                for (AbstractTransition abstractTransition : improvedApproximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus()) {
                    if (abstractTransition.getTarget().equals(q) && !MinusMarking.get(abstractTransition)) {
                        DeltaTMinus.add(abstractTransition);
                    }
                }
                if (!DeltaTMinus.isEmpty()) {
                    t = DeltaTMinus.iterator().next();
                    q = t.getSource();
                    e = t.getEvent();
                    q_ = t.getTarget();
                    MinusMarking.put(t, true);
                    if (DeltaTMinus.size() > 1) {
                        LinkedHashSet<AbstractTransition> tmpDeltaTMinus = new LinkedHashSet<>(DeltaTMinus);
                        tmpDeltaTMinus.remove(t);
                        Stack<Triplet<Event, ABoolExpr, AbstractState>> tmpP = (Stack<Triplet<Event, ABoolExpr, AbstractState>>) p.clone();
                        pb.push(new Tuple<>(tmpDeltaTMinus, tmpP));
                    }
                    p.push(new Triplet<>(e, wp_q_, q_));
                }
            }
            System.out.println(t);
            ConcreteState c;
            if ((c = findConcreteInstance(q, GREEN, wp_q_)) == null) {
                if ((c = findConcreteInstance(q, BLUE, wp_q_)) == null) {
                    c = findConcreteInstance(q, wp_q_);
                    improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getAlpha().put(c, q);
                    improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getKappa().put(c, BLUE);
                }
            }
            while (!p.isEmpty()) {
                Triplet<Event, ABoolExpr, AbstractState> triplet = p.pop();
                e = triplet.getFirst();
                wp_q_ = triplet.getSecond();
                q_ = triplet.getThird();
                z3.setCode(new And(
                        machine.getInvariant(),
                        machine.getInvariant().prime(),
                        c,
                        e.getSubstitution().getPrd(machine),
                        wp_q_.prime()
                ));
                if (z3.checkSAT() == SATISFIABLE) {
                    Model model = z3.getModel(machine.getAssignables());
                    ConcreteState c_ = new ConcreteState("c_" + q_.getName() + "_y", model.getTarget());
                    int c_Index = new ArrayList<>(improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC()).indexOf(c_);
                    c_.setName("c_" + q_.getName() + "_" + ((c_Index == -1) ? improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC().size() : c_Index));
                    improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC().add(c_);
                    improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getAlpha().put(c_, q_);
                    improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getKappa().put(c_, improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getKappa().get(c));
                    improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().add(new ConcreteTransition(c, e, c_));
                    c = c_;
                    q = q_;
                } else {
                    throw new Error("Impossible case occurred.");
                }
            }
            if (!pb.isEmpty()) {
                Tuple<LinkedHashSet<AbstractTransition>, Stack<Triplet<Event, ABoolExpr, AbstractState>>> tuple = pb.pop();
                DeltaTMinus = tuple.getFirst();
                p = tuple.getSecond();
                t = DeltaTMinus.iterator().next();
                q = t.getSource();
                e = t.getEvent();
                q_ = t.getTarget();
                MinusMarking.put(t, true);
                if (DeltaTMinus.size() > 1) {
                    LinkedHashSet<AbstractTransition> tmpDeltaTMinus = new LinkedHashSet<>(DeltaTMinus);
                    tmpDeltaTMinus.remove(t);
                    Stack<Triplet<Event, ABoolExpr, AbstractState>> tmpP = (Stack<Triplet<Event, ABoolExpr, AbstractState>>) p.clone();
                    pb.push(new Tuple<>(tmpDeltaTMinus, tmpP));
                }
                DeltaTMinus.clear();
                DeltaTMinus.add(t);
                wp_q_ = new And(
                        machine.getInvariant(),
                        machine.getInvariant().prime(),
                        q_,
                        p.peek().getFirst().getSubstitution().getPrd(machine),
                        wp_q_.prime()
                );
                p.push(new Triplet<>(e, wp_q_, q_));
            }
        }
    }

    private ConcreteState findConcreteInstance(AbstractState q, EConcreteStateColor color, ABoolExpr set) {
        LinkedHashSet<ConcreteState> FCS = improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getAlpha().keySet().stream().filter(c -> improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getAlpha().get(c).equals(q) && improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getKappa().get(c) == color).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!FCS.isEmpty()) {
            z3.setCode(new And(
                    machine.getInvariant(),
                    machine.getInvariant().prime(),
                    new Or(FCS.stream().toArray(ConcreteState[]::new)),
                    set
            ));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + q.getName() + "_" + new ArrayList<>(improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC()).indexOf(new ConcreteState("c_" + q.getName(), model.getSource())), model.getSource());
                improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC().add(c);
                improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getAlpha().put(c, q);
                improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getKappa().put(c, BLUE);
                return c;
            }
        }
        return null;
    }

    private ConcreteState findConcreteInstance(AbstractState q, ABoolExpr set) {
        z3.setCode(new And(
                machine.getInvariant(),
                machine.getInvariant().prime(),
                set
        ));
        if (z3.checkSAT() == SATISFIABLE) {
            Model model = z3.getModel();
            int cIndex = new ArrayList<>(improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC()).indexOf(new ConcreteState("c_" + q.getName(), model.getSource()));
            if (cIndex != -1) {
                return new ArrayList<>(improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC()).get(cIndex);
            } else {
                return new ConcreteState("c_" + q.getName() + "_" + improvedApproximatedTransitionSystem.getConcreteTransitionSystem().getC().size(), model.getSource());
            }
        } else {
            throw new Error("Impossible case occurred: unable to find concrete instance.");
        }
    }

    public ApproximatedTransitionSystem getImprovedApproximatedTransitionSystem() {
        return improvedApproximatedTransitionSystem;
    }

    private Machine getMachine() {
        return machine;
    }

}
