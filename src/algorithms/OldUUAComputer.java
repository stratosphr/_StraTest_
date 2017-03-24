package algorithms;

import algorithms.heuristics.EConcreteStateColor;
import algorithms.outputs.ApproximatedTransitionSystem;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.arith.IntVariable;
import eventb.exprs.arith.QuantifiedVariable;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Exists;
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
public final class OldUUAComputer extends AComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final ApproximatedTransitionSystem approximatedTransitionSystem;
    private final ApproximatedTransitionSystem improvedApproximatedTransitionSystem;
    private final LinkedHashMap<AbstractTransition, Boolean> MinusMarking;
    private final LinkedHashMap<AbstractTransition, Boolean> PlusMarking;
    private final Stack<Triplet<ABoolExpr, Event, AbstractState>> stack;
    private final Z3 z3;

    public OldUUAComputer(Machine machine, ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.machine = machine;
        this.approximatedTransitionSystem = approximatedTransitionSystem;
        this.improvedApproximatedTransitionSystem = approximatedTransitionSystem.clone();
        this.MinusMarking = new LinkedHashMap<>(improvedApproximatedTransitionSystem.get3MTS().getDeltaMinus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.PlusMarking = new LinkedHashMap<>(improvedApproximatedTransitionSystem.get3MTS().getDeltaPlus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.stack = new Stack<>();
        this.z3 = new Z3();
    }

    @Override
    protected ApproximatedTransitionSystem compute_() {
        LinkedHashSet<AbstractTransition> DeltaTMinus = new LinkedHashSet<>();
        improvedApproximatedTransitionSystem.get3MTS().getDeltaMinus().forEach(t -> {
            if (!MinusMarking.get(t) && improvedApproximatedTransitionSystem.get3MTS().getDeltaMinus().stream().noneMatch(abstractTransition -> abstractTransition.getSource().equals(t.getTarget()))) {
                DeltaTMinus.add(t);
            }
        });
        DeltaTMinus.forEach(this::mustMinusConcretization);
        improvedApproximatedTransitionSystem.get3MTS().getDeltaMinus().forEach(t -> {
            if (!MinusMarking.get(t)) {
                mustMinusConcretization(t);
            }
        });
        // Step 1: t has not been concretized yet and is an entry point for a must+ structure
        PlusMarking.forEach((t, marked) -> {
            if (!marked && isMustPlusStructureEntryPoint(t)) {
                Optional<ConcreteState> optionalC = improvedApproximatedTransitionSystem.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedApproximatedTransitionSystem.getCTS().getAlpha().get(concreteState).equals(t.getSource()) && improvedApproximatedTransitionSystem.getCTS().getKappa().get(concreteState) == GREEN).findFirst();
                if (optionalC.isPresent()) {
                    ConcreteState c = optionalC.get();
                    mustPlusConcretization(t, c, machine);
                } else {
                    optionalC = improvedApproximatedTransitionSystem.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedApproximatedTransitionSystem.getCTS().getAlpha().get(concreteState).equals(t.getSource())).findFirst();
                    if (optionalC.isPresent()) {
                        ConcreteState c = optionalC.get();
                        mustPlusConcretization(t, c, machine);
                    }
                }
            }
        });
        // Step 3: concretisation of the must+ structures with no entry point
        PlusMarking.forEach((t, marked) -> {
            if (!marked) {
                Optional<ConcreteState> optionalC = improvedApproximatedTransitionSystem.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedApproximatedTransitionSystem.getCTS().getAlpha().get(concreteState).equals(t.getSource()) && improvedApproximatedTransitionSystem.getCTS().getKappa().get(concreteState) == GREEN).findFirst();
                if (optionalC.isPresent()) {
                    ConcreteState c = optionalC.get();
                    mustPlusConcretization(t, c, machine);
                } else {
                    optionalC = improvedApproximatedTransitionSystem.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedApproximatedTransitionSystem.getCTS().getAlpha().get(concreteState).equals(t.getSource())).findFirst();
                    if (optionalC.isPresent()) {
                        ConcreteState c = optionalC.get();
                        mustPlusConcretization(t, c, machine);
                    }
                }
            }
        });
        /*approximatedTransitionSystem.get3MTS().getDelta().forEach(abstractTransition -> {
            System.out.println(abstractTransition);
        });
        System.out.println();
        System.out.println(getImprovedATS().getCTS().getDeltaC().size());
        getImprovedATS().getCTS().getDeltaC().forEach(concreteTransition -> {
            AbstractTransition abstractTransition = new AbstractTransition(improvedApproximatedTransitionSystem.getCTS().getAlpha().get(concreteTransition.getSource()), concreteTransition.getEvent(), improvedApproximatedTransitionSystem.getCTS().getAlpha().get(concreteTransition.getTarget()));
            if (!getImprovedATS().get3MTS().getDelta().contains(abstractTransition)) {
                throw new Error("There is a new abstract transition: " + abstractTransition);
            }
        });*/
        return getImprovedApproximatedTransitionSystem();
    }

    private void mustPlusConcretization(AbstractTransition t, ConcreteState c, Machine machine) {
        Set<ConcreteTransition> TC = new LinkedHashSet<>();
        ConcreteTransition tc;
        improvedApproximatedTransitionSystem.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedApproximatedTransitionSystem.getCTS().getAlpha().get(concreteState).equals(t.getTarget())).forEach(concreteState -> TC.addAll(improvedApproximatedTransitionSystem.getCTS().getDeltaC().stream().filter(concreteTransition -> concreteTransition.getSource().equals(c) && concreteTransition.getEvent().getName().equals(t.getEvent().getName()) && concreteTransition.getTarget().equals(concreteState)).collect(Collectors.toList())));
        if (!TC.isEmpty()) {
            tc = TC.stream().filter(concreteTransition -> improvedApproximatedTransitionSystem.getCTS().getKappa().get(concreteTransition.getTarget()) == GREEN).findAny().orElse(TC.iterator().next());
        } else {
            z3.setCode(new And(
                    machine.getInvariant().prime(),
                    new Exists(
                            new And(
                                    machine.getInvariant(),
                                    t.getEvent().getSubstitution().getPrd(machine),
                                    c.getExpression()
                            ),
                            machine.getAssignables().stream().filter(assignable -> assignable instanceof IntVariable).map(assignable -> new QuantifiedVariable((IntVariable) assignable)).toArray(QuantifiedVariable[]::new)
                    ),
                    t.getTarget().prime()
            ));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c_ = new ConcreteState("c_" + t.getTarget().getName(), model.getTarget());
                if (improvedApproximatedTransitionSystem.getCTS().getC().add(c_)) {
                    improvedApproximatedTransitionSystem.getCTS().getAlpha().put(c_, t.getTarget());
                    improvedApproximatedTransitionSystem.getCTS().getKappa().put(c_, GREEN);
                }
                tc = new ConcreteTransition(c, t.getEvent(), c_);
                improvedApproximatedTransitionSystem.getCTS().getDeltaC().add(tc);
            } else {
                throw new Error("Impossible in mustPlusConcretization");
            }
        }
        PlusMarking.put(t, true);
        PlusMarking.forEach((t2, marked) -> {
            if (!marked && t2.getSource().equals(t.getTarget())) {
                mustPlusConcretization(t2, tc.getTarget(), machine);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void mustMinusConcretization(AbstractTransition t) {
        System.err.println(t);
        AbstractState q = t.getSource();
        Event e = t.getEvent();
        AbstractState q_ = t.getTarget();
        Stack<Triplet<Event, ABoolExpr, AbstractState>> p = new Stack<>();
        Stack<Tuple<LinkedHashSet<AbstractTransition>, Stack<Triplet<Event, ABoolExpr, AbstractState>>>> pb = new Stack<>();
        p.push(new Triplet<>(e, q_, q_));
        LinkedHashSet<AbstractTransition> DeltaTMinus = new LinkedHashSet<>();
        DeltaTMinus.add(t);
        ABoolExpr wp_q_ = new And(machine.getInvariant(), q_.clone());
        MinusMarking.put(t, true);
        while (!DeltaTMinus.isEmpty()) {
            while (!DeltaTMinus.isEmpty()) {
                System.err.println(wp_q_.prime());
                System.err.println("#########");
                z3.setCode(wp_q_);
                System.err.println(z3.checkSAT());
                wp_q_ = new And(
                        machine.getInvariant(),
                        machine.getInvariant().prime(),
                        q.clone(),
                        e.getSubstitution().getPrd(machine),
                        wp_q_.prime()
                );
                DeltaTMinus.clear();
                for (AbstractTransition abstractTransition : improvedApproximatedTransitionSystem.get3MTS().getDeltaMinus()) {
                    if (abstractTransition.getTarget().equals(q) && !MinusMarking.get(abstractTransition)) {
                        DeltaTMinus.add(abstractTransition);
                    }
                }
                if (!DeltaTMinus.isEmpty()) {
                    t = DeltaTMinus.iterator().next().clone();
                    System.err.println(t);
                    q = t.getSource().clone();
                    e = t.getEvent().clone();
                    q_ = t.getTarget().clone();
                    MinusMarking.put(t, true);
                    if (DeltaTMinus.size() > 1) {
                        LinkedHashSet<AbstractTransition> tmpDeltaTMinus = new LinkedHashSet<>(DeltaTMinus);
                        tmpDeltaTMinus.remove(t);
                        Stack<Triplet<Event, ABoolExpr, AbstractState>> tmpP = (Stack<Triplet<Event, ABoolExpr, AbstractState>>) p.clone();
                        pb.push(new Tuple<>(tmpDeltaTMinus, tmpP));
                    }
                    p.push(new Triplet<>(e.clone(), wp_q_.clone(), q_));
                }
            }
            ConcreteState c = null;
            boolean gp = false;
            while (!p.isEmpty()) {
                if (!gp) {
                    if ((c = findConcreteInstance(q, GREEN, wp_q_)) == null) {
                        if ((c = findConcreteInstance(q, BLUE, wp_q_)) == null) {
                            c = findConcreteInstance(q, wp_q_);
                            z3.setCode(new And(
                                    machine.getInvariant(),
                                    machine.getInvariant().prime(),
                                    c,
                                    machine.getInitialisation().getPrd(getMachine())
                            ));
                            if (z3.checkSAT() == SATISFIABLE) {
                                improvedApproximatedTransitionSystem.getCTS().getKappa().put(c, GREEN);
                                gp = true;
                            } else {
                                improvedApproximatedTransitionSystem.getCTS().getKappa().put(c, BLUE);
                            }
                            improvedApproximatedTransitionSystem.getCTS().getC().add(c);
                            improvedApproximatedTransitionSystem.getCTS().getAlpha().put(c, q);
                        }
                    } else {
                        gp = true;
                    }
                }
                if (c == null) {
                    throw new Error("Impossible case occurred: c is null.");
                }
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
                    int c_Index = new ArrayList<>(improvedApproximatedTransitionSystem.getCTS().getC()).indexOf(c_);
                    c_.setName("c_" + q_.getName() + "_" + ((c_Index == -1) ? improvedApproximatedTransitionSystem.getCTS().getC().size() : c_Index));
                    improvedApproximatedTransitionSystem.getCTS().getC().add(c_);
                    improvedApproximatedTransitionSystem.getCTS().getAlpha().put(c_, q_);
                    improvedApproximatedTransitionSystem.getCTS().getKappa().put(c_, improvedApproximatedTransitionSystem.getCTS().getKappa().get(c));
                    improvedApproximatedTransitionSystem.getCTS().getDeltaC().add(new ConcreteTransition(c, e, c_));
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
                System.err.println("");
                System.err.println(t);
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
                        //wp_q_.prime().prime() works for threeBatteries 4
                );
                p.push(new Triplet<>(e.clone(), wp_q_.clone(), q_.clone()));
            }
        }
    }

    private ConcreteState findConcreteInstance(AbstractState q, EConcreteStateColor color, ABoolExpr set) {
        LinkedHashSet<ConcreteState> FCS = improvedApproximatedTransitionSystem.getCTS().getAlpha().keySet().stream().filter(c -> improvedApproximatedTransitionSystem.getCTS().getAlpha().get(c).equals(q) && improvedApproximatedTransitionSystem.getCTS().getKappa().get(c) == color).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!FCS.isEmpty()) {
            z3.setCode(new And(
                    machine.getInvariant(),
                    machine.getInvariant().prime(),
                    new Or(FCS.stream().toArray(ConcreteState[]::new)),
                    set
            ));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + q.getName() + "_" + new ArrayList<>(improvedApproximatedTransitionSystem.getCTS().getC()).indexOf(new ConcreteState("c_" + q.getName(), model.getSource())), model.getSource());
                improvedApproximatedTransitionSystem.getCTS().getC().add(c);
                improvedApproximatedTransitionSystem.getCTS().getAlpha().put(c, q);
                improvedApproximatedTransitionSystem.getCTS().getKappa().put(c, BLUE);
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
            int cIndex = new ArrayList<>(improvedApproximatedTransitionSystem.getCTS().getC()).indexOf(new ConcreteState("c_" + q.getName(), model.getSource()));
            if (cIndex != -1) {
                return new ArrayList<>(improvedApproximatedTransitionSystem.getCTS().getC()).get(cIndex);
            } else {
                return new ConcreteState("c_" + q.getName() + "_" + improvedApproximatedTransitionSystem.getCTS().getC().size(), model.getSource());
            }
        } else {
            throw new Error("Impossible case occurred: unable to find concrete instance.");
        }
    }

    private boolean isMustPlusStructureEntryPoint(AbstractTransition t) {
        return improvedApproximatedTransitionSystem.get3MTS().getDeltaPlus().stream().noneMatch(abstractTransition -> t.getSource().equals(abstractTransition.getTarget()));
    }

    public ApproximatedTransitionSystem getImprovedApproximatedTransitionSystem() {
        return improvedApproximatedTransitionSystem;
    }

    private Machine getMachine() {
        return machine;
    }

}
