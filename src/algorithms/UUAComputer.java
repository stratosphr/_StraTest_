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
public final class UUAComputer extends AComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final ApproximatedTransitionSystem approximatedTransitionSystem;
    private final ApproximatedTransitionSystem improvedATS;
    private final LinkedHashMap<AbstractTransition, Boolean> MinusMarking;
    private final LinkedHashMap<AbstractTransition, Boolean> PlusMarking;
    private final Stack<Triplet<ABoolExpr, Event, AbstractState>> stack;
    private final Z3 z3;

    public UUAComputer(Machine machine, ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.machine = machine;
        this.approximatedTransitionSystem = approximatedTransitionSystem.clone();
        this.improvedATS = approximatedTransitionSystem.clone();
        this.MinusMarking = new LinkedHashMap<>(improvedATS.get3MTS().getDeltaMinus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.PlusMarking = new LinkedHashMap<>(improvedATS.get3MTS().getDeltaPlus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.stack = new Stack<>();
        this.z3 = new Z3();
    }

    @Override
    protected ApproximatedTransitionSystem compute_() {
        // Step 1: t has not been concretized yet and is an entry point for a must+ structure
        PlusMarking.forEach((t, marked) -> {
            if (!marked && isMustPlusStructureEntryPoint(t)) {
                Optional<ConcreteState> optionalC = improvedATS.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedATS.getCTS().getAlpha().get(concreteState).equals(t.getSource()) && improvedATS.getCTS().getKappa().get(concreteState) == GREEN).findFirst();
                if (optionalC.isPresent()) {
                    ConcreteState c = optionalC.get();
                    mustPlusConcretization(t, c, machine);
                } else {
                    optionalC = improvedATS.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedATS.getCTS().getAlpha().get(concreteState).equals(t.getSource())).findFirst();
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
                Optional<ConcreteState> optionalC = improvedATS.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedATS.getCTS().getAlpha().get(concreteState).equals(t.getSource()) && improvedATS.getCTS().getKappa().get(concreteState) == GREEN).findFirst();
                if (optionalC.isPresent()) {
                    ConcreteState c = optionalC.get();
                    mustPlusConcretization(t, c, machine);
                } else {
                    optionalC = improvedATS.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedATS.getCTS().getAlpha().get(concreteState).equals(t.getSource())).findFirst();
                    if (optionalC.isPresent()) {
                        ConcreteState c = optionalC.get();
                        mustPlusConcretization(t, c, machine);
                    }
                }
            }
        });
        LinkedHashSet<AbstractTransition> DeltaTMinus = new LinkedHashSet<>();
        for (AbstractTransition t : approximatedTransitionSystem.get3MTS().getDeltaMinus()) {
            if (!MinusMarking.get(t) && approximatedTransitionSystem.get3MTS().getDeltaMinus().stream().noneMatch(abstractTransition -> abstractTransition.getSource().equals(t.getTarget()))) {
                DeltaTMinus.add(t);
            }
        }
        DeltaTMinus.forEach(this::mustMinusConcretization);
        for (AbstractTransition t : approximatedTransitionSystem.get3MTS().getDeltaMinus()) {
            if (!MinusMarking.get(t)) {
                mustMinusConcretization(t);
            }
        }
        return getImprovedATS();
    }

    @SuppressWarnings("unchecked")
    private void mustMinusConcretization(AbstractTransition abstractTransition) {
        Stack<Triplet<Event, ABoolExpr, AbstractState>> p = new Stack<>();
        p.push(new Triplet<>(abstractTransition.getEvent(), abstractTransition.getTarget(), abstractTransition.getTarget()));
        Stack<Tuple<LinkedHashSet<AbstractTransition>, Stack<Triplet<Event, ABoolExpr, AbstractState>>>> pb = new Stack<>();
        LinkedHashSet<AbstractTransition> DeltaTMinus = new LinkedHashSet<>(Collections.singletonList(abstractTransition));
        AbstractTransition t = abstractTransition.clone();
        MinusMarking.put(t, true);
        AbstractState q = t.getSource();
        Event e = t.getEvent();
        AbstractState q_ = t.getTarget();
        ABoolExpr wp_q_ = new And(machine.getInvariant(), q_);
        while (!DeltaTMinus.isEmpty()) {
            while (!DeltaTMinus.isEmpty()) {
                wp_q_ = new And(
                        machine.getInvariant(),
                        q,
                        e.getSubstitution().getPrd(machine),
                        wp_q_.prime()
                );
                DeltaTMinus = new LinkedHashSet<>();
                for (AbstractTransition _t : MinusMarking.keySet()) {
                    if (!MinusMarking.get(_t) && _t.getTarget().equals(q)) {
                        DeltaTMinus.add(_t);
                    }
                }
                if (!DeltaTMinus.isEmpty()) {
                    t = DeltaTMinus.iterator().next().clone();
                    q = t.getSource();
                    e = t.getEvent();
                    q_ = t.getTarget();
                    MinusMarking.put(t, true);
                    LinkedHashSet<AbstractTransition> tmpDeltaTMinus = new LinkedHashSet<>(DeltaTMinus);
                    tmpDeltaTMinus.remove(t);
                    if (!tmpDeltaTMinus.isEmpty()) {
                        pb.push(new Tuple<>(tmpDeltaTMinus, (Stack<Triplet<Event, ABoolExpr, AbstractState>>) p.clone()));
                    }
                    p.push(new Triplet<>(e, wp_q_, q_));

                }
            }
            boolean gp = false;
            ConcreteState c;
            if ((c = findConcreteInstance(q, GREEN, wp_q_)) == null) {
                if ((c = findConcreteInstance(q, BLUE, wp_q_)) == null) {
                    if ((c = findConcreteInstance(q, wp_q_)) != null) {
                        z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), machine.getInitialisation().getPrd(machine), c.prime()));
                        if (z3.checkSAT() == SATISFIABLE) {
                            improvedATS.getCTS().getC0().add(c);
                            improvedATS.getCTS().getKappa().put(c, GREEN);
                            gp = true;
                        } else {
                            improvedATS.getCTS().getKappa().putIfAbsent(c, BLUE);
                        }
                    } else {
                        throw new Error("Impossible case occurred: no concrete instance found.");
                    }
                    improvedATS.getCTS().getKappa().putIfAbsent(c, BLUE);
                }
                improvedATS.getCTS().getKappa().putIfAbsent(c, BLUE);
            } else {
                gp = true;
            }
            improvedATS.getCTS().getC().add(c);
            improvedATS.getCTS().getAlpha().put(c, q);
            while (!p.isEmpty()) {
                if (!gp) {
                    if ((c = findConcreteInstance(q, GREEN, wp_q_)) == null) {
                        if ((c = findConcreteInstance(q, BLUE, wp_q_)) == null) {
                            if ((c = findConcreteInstance(q, wp_q_)) != null) {
                                z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), machine.getInitialisation().getPrd(machine), c.prime()));
                                if (z3.checkSAT() == SATISFIABLE) {
                                    improvedATS.getCTS().getC0().add(c);
                                    improvedATS.getCTS().getKappa().put(c, GREEN);
                                    gp = true;
                                } else {
                                    improvedATS.getCTS().getKappa().putIfAbsent(c, BLUE);
                                }
                            } else {
                                throw new Error("Impossible case occurred: no concrete instance found.");
                            }
                            improvedATS.getCTS().getKappa().putIfAbsent(c, BLUE);
                        }
                        improvedATS.getCTS().getKappa().putIfAbsent(c, BLUE);
                    } else {
                        gp = true;
                    }
                    improvedATS.getCTS().getC().add(c);
                    improvedATS.getCTS().getAlpha().put(c, q);
                }
                Triplet<Event, ABoolExpr, AbstractState> triplet = p.pop();
                e = triplet.getFirst().clone();
                wp_q_ = triplet.getSecond().clone();
                q_ = triplet.getThird().clone();
                z3.setCode(new And(
                        machine.getInvariant(),
                        c,
                        e.getSubstitution().getPrd(machine),
                        wp_q_.prime()
                ));
                if (z3.checkSAT() == SATISFIABLE) {
                    Model model = z3.getModel(machine.getAssignables());
                    ConcreteState c_;
                    int c_Index = new ArrayList<>(improvedATS.getCTS().getC()).indexOf(new ConcreteState("c_" + q_.getName(), model.getTarget()));
                    if (c_Index != -1) {
                        c_ = new ConcreteState("c_" + q_.getName() + "_" + c_Index, model.getTarget());
                    } else {
                        c_ = new ConcreteState("c_" + q_.getName() + "_" + improvedATS.getCTS().getC().size(), model.getTarget());
                    }
                    if (!improvedATS.getCTS().getKappa().containsKey(c_) || improvedATS.getCTS().getKappa().get(c_) != GREEN) {
                        improvedATS.getCTS().getKappa().put(c_, improvedATS.getCTS().getKappa().containsKey(c) ? improvedATS.getCTS().getKappa().get(c) : BLUE);
                    }
                    improvedATS.getCTS().getAlpha().put(c_, q_);
                    improvedATS.getCTS().getDeltaC().add(new ConcreteTransition(c, e, c_));
                    improvedATS.getCTS().getC().add(c_);
                    c = c_;
                    q = q_;
                } else {
                    throw new Error("Impossible case occurred: no concrete transition from c found.");
                }
            }
            if (!pb.isEmpty()) {
                Tuple<LinkedHashSet<AbstractTransition>, Stack<Triplet<Event, ABoolExpr, AbstractState>>> tuple = pb.pop();
                DeltaTMinus = new LinkedHashSet<>(tuple.getFirst());
                p = (Stack<Triplet<Event, ABoolExpr, AbstractState>>) tuple.getSecond().clone();
                t = DeltaTMinus.iterator().next();
                q = t.getSource();
                e = t.getEvent();
                q_ = t.getTarget();
                MinusMarking.put(t, true);
                LinkedHashSet<AbstractTransition> tmpDeltaTMinus = new LinkedHashSet<>(DeltaTMinus);
                tmpDeltaTMinus.remove(t);
                if (!tmpDeltaTMinus.isEmpty()) {
                    pb.push(new Tuple<>(tmpDeltaTMinus, (Stack<Triplet<Event, ABoolExpr, AbstractState>>) p.clone()));
                }
                DeltaTMinus = new LinkedHashSet<>(Collections.singletonList(t));
                wp_q_ = new And(
                        machine.getInvariant(),
                        machine.getInvariant().prime(),
                        q_,
                        p.peek().getFirst().getSubstitution().getPrd(machine),
                        p.peek().getSecond().prime()
                );
                p.push(new Triplet<>(e, wp_q_, q_));
            }
        }
    }

    private ConcreteState findConcreteInstance(AbstractState q, EConcreteStateColor color, ABoolExpr set) {
        LinkedHashSet<ConcreteState> FCS = improvedATS.getCTS().getAlpha().keySet().stream().filter(c -> improvedATS.getCTS().getAlpha().get(c).equals(q) && improvedATS.getCTS().getKappa().get(c) == color).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!FCS.isEmpty()) {
            z3.setCode(new And(
                    machine.getInvariant(),
                    machine.getInvariant().prime(),
                    new Or(FCS.stream().toArray(ConcreteState[]::new)),
                    set
            ));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                return new ConcreteState("c_" + q.getName() + "_" + new ArrayList<>(improvedATS.getCTS().getC()).indexOf(new ConcreteState("c_" + q.getName(), model.getSource())), model.getSource());
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
            int cIndex = new ArrayList<>(improvedATS.getCTS().getC()).indexOf(new ConcreteState("c_" + q.getName(), model.getSource()));
            if (cIndex != -1) {
                return new ArrayList<>(improvedATS.getCTS().getC()).get(cIndex);
            } else {
                return new ConcreteState("c_" + q.getName() + "_" + improvedATS.getCTS().getC().size(), model.getSource());
            }
        } else {
            throw new Error("Impossible case occurred: unable to find concrete instance.");
        }
    }

    private void mustPlusConcretization(AbstractTransition t, ConcreteState c, Machine machine) {
        LinkedHashSet<ConcreteState> BCS = improvedATS.getCTS().getKappa().keySet().stream().filter(concreteState -> improvedATS.getCTS().getAlpha().get(concreteState).equals(t.getTarget()) && improvedATS.getCTS().getKappa().get(concreteState) == BLUE).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!BCS.isEmpty()) {
            z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), c, t.getEvent().getSubstitution().getPrd(machine), new Or(BCS.stream().toArray(ConcreteState[]::new)).prime()));
        }
        if (BCS.isEmpty() || z3.checkSAT() != SATISFIABLE) {
            z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), c, t.getEvent().getSubstitution().getPrd(machine), t.getTarget().prime()));
            if (z3.checkSAT() != SATISFIABLE) {
                throw new Error("Impossible case occurred during must+ concretization: unable to find a concrete instance of a must+ transition. (" + z3.checkSAT() + ")");
            }
        }
        Model model = z3.getModel();
        ConcreteState c_ = new ConcreteState("c_" + t.getTarget().getName(), model.getTarget());
        int c_Index = new ArrayList<>(improvedATS.getCTS().getC()).indexOf(c_);
        c_.setName("c_" + t.getTarget().getName() + "_" + ((c_Index == -1) ? improvedATS.getCTS().getC().size() : c_Index));
        ConcreteTransition concreteTransition = new ConcreteTransition(c, t.getEvent(), c_);
        improvedATS.getCTS().getC().add(c_);
        improvedATS.getCTS().getAlpha().put(c_, t.getTarget());
        if (!improvedATS.getCTS().getKappa().containsKey(c_) || improvedATS.getCTS().getKappa().get(c_) != GREEN) {
            improvedATS.getCTS().getKappa().put(c_, improvedATS.getCTS().getKappa().get(c));
        }
        improvedATS.getCTS().getDeltaC().add(concreteTransition);
        PlusMarking.put(t, true);
        PlusMarking.forEach((t2, marked) -> {
            if (!marked && t2.getSource().equals(t.getTarget())) {
                mustPlusConcretization(t2, concreteTransition.getTarget(), machine);
            }
        });
    }

    /*private void mustPlusConcretization(AbstractTransition t, ConcreteState c, Machine machine) {
        Set<ConcreteTransition> TC = new LinkedHashSet<>();
        ConcreteTransition tc;
        improvedATS.getCTS().getAlpha().keySet().stream().filter(concreteState -> improvedATS.getCTS().getAlpha().get(concreteState).equals(t.getTarget())).forEach(concreteState -> TC.addAll(improvedATS.getCTS().getDeltaC().stream().filter(concreteTransition -> concreteTransition.getSource().equals(c) && concreteTransition.getEvent().getName().equals(t.getEvent().getName()) && concreteTransition.getTarget().equals(concreteState)).collect(Collectors.toList())));
        if (!TC.isEmpty()) {
            tc = TC.stream().filter(concreteTransition -> improvedATS.getCTS().getKappa().get(concreteTransition.getTarget()) == GREEN).findAny().orElse(TC.iterator().next());
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
                int c_Index = new ArrayList<>(improvedATS.getCTS().getC()).indexOf(c_);
                c_.setName("c_" + t.getTarget().getName() + "_" + ((c_Index == -1) ? improvedATS.getCTS().getC().size() : c_Index));
                if (improvedATS.getCTS().getC().add(c_)) {
                    improvedATS.getCTS().getAlpha().put(c_, t.getTarget());
                    improvedATS.getCTS().getKappa().put(c_, GREEN);
                }
                tc = new ConcreteTransition(c, t.getEvent(), c_);
                improvedATS.getCTS().getDeltaC().add(tc);
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
    }*/

    private boolean isMustPlusStructureEntryPoint(AbstractTransition t) {
        return improvedATS.get3MTS().getDeltaPlus().stream().noneMatch(abstractTransition -> t.getSource().equals(abstractTransition.getTarget()));
    }

    public ApproximatedTransitionSystem getImprovedATS() {
        return improvedATS;
    }

    private Machine getMachine() {
        return machine;
    }

}
