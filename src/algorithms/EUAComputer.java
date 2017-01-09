package algorithms;

import algorithms.heuristics.*;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ConcreteTransitionSystem;
import algorithms.outputs.EventSystem;
import algorithms.outputs.TriModalTransitionSystem;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.arith.IntVariable;
import eventb.exprs.arith.QuantifiedVariable;
import eventb.exprs.bool.*;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import solvers.z3.Model;
import solvers.z3.Z3;
import utilities.sets.Tuple;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static algorithms.heuristics.EConcreteStateColor.BLUE;
import static algorithms.heuristics.EConcreteStateColor.GREEN;
import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:36
 */
public final class EUAComputer extends AComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final LinkedHashSet<AbstractState> A;
    private final IEventsOrderingFunction eventsOrderingFunction;
    private final IAbstractStatesOrderingFunction abstractStatesOrderingFunction;
    private final EEUAComputerHeuristics heuristics;
    private final LinkedHashSet<AbstractState> RQ;
    private final LinkedHashSet<AbstractState> Q0;
    private final LinkedHashSet<AbstractState> Q;
    private final LinkedHashSet<AbstractTransition> Delta;
    private final LinkedHashSet<AbstractTransition> DeltaMinus;
    private final LinkedHashSet<AbstractTransition> DeltaPlus;
    private final LinkedHashSet<ConcreteState> C0;
    private final LinkedHashSet<ConcreteState> C;
    private final LinkedHashMap<ConcreteState, AbstractState> Alpha;
    private final LinkedHashMap<ConcreteState, EConcreteStateColor> Kappa;
    private final LinkedHashSet<ConcreteTransition> DeltaC;
    private final Z3 z3;

    public EUAComputer(Machine machine, LinkedHashSet<AbstractState> A, EEUAComputerHeuristics heuristics) {
        this(machine, A, new DefaultAbstractStatesOrderingFunction(), new DefaultEventsOrderingFunction(), heuristics);
    }

    public EUAComputer(Machine machine, LinkedHashSet<AbstractState> A, IAbstractStatesOrderingFunction abstractStatesOrderingFunction, EEUAComputerHeuristics heuristics) {
        this(machine, A, abstractStatesOrderingFunction, new DefaultEventsOrderingFunction(), heuristics);
    }

    public EUAComputer(Machine machine, LinkedHashSet<AbstractState> A, IEventsOrderingFunction eventsOrderingFunction, EEUAComputerHeuristics heuristics) {
        this(machine, A, new DefaultAbstractStatesOrderingFunction(), eventsOrderingFunction, heuristics);
    }

    public EUAComputer(Machine machine, LinkedHashSet<AbstractState> A, IAbstractStatesOrderingFunction abstractStatesOrderingFunction, IEventsOrderingFunction eventsOrderingFunction, EEUAComputerHeuristics heuristics) {
        this.machine = machine;
        this.A = A;
        this.eventsOrderingFunction = eventsOrderingFunction;
        this.abstractStatesOrderingFunction = abstractStatesOrderingFunction;
        this.heuristics = heuristics;
        this.RQ = new LinkedHashSet<>();
        this.Q0 = new LinkedHashSet<>();
        this.Q = new LinkedHashSet<>();
        this.Delta = new LinkedHashSet<>();
        this.DeltaMinus = new LinkedHashSet<>();
        this.DeltaPlus = new LinkedHashSet<>();
        this.C0 = new LinkedHashSet<>();
        this.C = new LinkedHashSet<>();
        this.Alpha = new LinkedHashMap<>();
        this.Kappa = new LinkedHashMap<>();
        this.DeltaC = new LinkedHashSet<>();
        this.z3 = new Z3();
    }

    @Override
    public ApproximatedTransitionSystem compute_() {
        step1();
        step2();
        EventSystem eventSystem = new EventSystem(getMachine().getAssignables(), getMachine().getInvariant(), getMachine().getInitialisation(), getMachine().getEvents());
        TriModalTransitionSystem triModalTransitionSystem = new TriModalTransitionSystem(getQ0(), getQ(), getDelta(), getDeltaMinus(), getDeltaPlus());
        ConcreteTransitionSystem concreteTransitionSystem = new ConcreteTransitionSystem(getC0(), getC(), getAlpha(), getKappa(), getDeltaC());
        return new ApproximatedTransitionSystem(eventSystem, triModalTransitionSystem, concreteTransitionSystem);
    }

    private void step1() {
        for (AbstractState q : getA()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), getMachine().getInitialisation().getPrd(getMachine()), q.prime()));
            if (z3.checkSAT() == SATISFIABLE) {
                ConcreteState c = new ConcreteState("c_" + q.getName(), z3.getModel().getTarget());
                Q0.add(q);
                C0.add(c);
                Alpha.put(c, q);
                Kappa.put(c, GREEN);
            }
        }
        C.addAll(C0);
    }

    private void step2() {
        RQ.addAll(getQ0());
        while (!RQ.isEmpty()) {
            AbstractState q = RQ.iterator().next();
            RQ.remove(q);
            Q.add(q);
            for (AbstractState q_ : abstractStatesOrderingFunction.apply(new Tuple<>(q, getA()))) {
                for (Event e : eventsOrderingFunction.apply(getMachine().getEvents())) {
                    z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), q, e.getSubstitution().getPrd(getMachine()), q_.prime()));
                    if (z3.checkSAT() == SATISFIABLE) {
                        Model model = z3.getModel();
                        AbstractTransition abstractTransition = new AbstractTransition(q, e, q_);
                        getDelta().add(abstractTransition);
                        registerMustMinus(abstractTransition);
                        registerMustPlus(abstractTransition);
                        switch (heuristics) {
                            case OLD_EXCLUSIVE:
                                if (!instantiateFromBlueToAny(abstractTransition)) {
                                    instantiateFromWitnesses(abstractTransition, model);
                                }
                                break;
                            case OLD_EXHAUSTIVE:
                                instantiateFromBlueToAny(abstractTransition);
                                instantiateFromWitnesses(abstractTransition, model);
                                break;
                            case EXCLUSIVE:
                                if (!instantiateFromGreenToBlue(abstractTransition)) {
                                    if (!instantiateFromGreenToAny(abstractTransition)) {
                                        if (!instantiateFromBlueToBlue(abstractTransition)) {
                                            if (!instantiateFromBlueToAny(abstractTransition)) {
                                                instantiateFromWitnesses(abstractTransition, model);
                                            }
                                        }
                                    }
                                }
                                break;
                            case EXHAUSTIVE:
                                instantiateFromGreenToBlue(abstractTransition);
                                instantiateFromGreenToAny(abstractTransition);
                                instantiateFromBlueToBlue(abstractTransition);
                                instantiateFromBlueToAny(abstractTransition);
                                instantiateFromWitnesses(abstractTransition, model);
                                break;
                        }
                        if (!Q.contains(q_)) {
                            RQ.add(q_);
                        }
                    }
                }
            }
        }
    }

    private boolean registerMustMinus(AbstractTransition abstractTransition) {
        z3.setCode(new And(getMachine().getInvariant().prime(), new Not(new Exists(new And(getMachine().getInvariant(), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getSource()), getMachine().getQuantifiedVariables().stream().toArray(QuantifiedVariable[]::new))), abstractTransition.getTarget().getExpression().prime()));
        if (z3.checkSAT() == UNSATISFIABLE) {
            DeltaMinus.add(abstractTransition);
            return true;
        }
        return false;
    }

    private boolean registerMustPlus(AbstractTransition abstractTransition) {
        z3.setCode(new And(getMachine().getInvariant(), abstractTransition.getSource(), new Not(new Exists(new And(getMachine().getInvariant().prime(), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getTarget().prime()), getMachine().getQuantifiedVariables().stream().map(quantifiedVariable -> new QuantifiedVariable((IntVariable) new IntVariable(quantifiedVariable.getName()).prime())).toArray(QuantifiedVariable[]::new)))));
        if (z3.checkSAT() == UNSATISFIABLE) {
            DeltaPlus.add(abstractTransition);
            return true;
        }
        return false;
    }

    public boolean instantiateFromGreenToBlue(AbstractTransition abstractTransition) {
        LinkedHashSet<ConcreteState> GCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(GREEN) && getAlpha().get(concreteState).equals(abstractTransition.getSource())).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<ConcreteState> BCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(BLUE) && getAlpha().get(concreteState).equals(abstractTransition.getTarget())).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!GCS.isEmpty() && !BCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(GCS.stream().toArray(ABoolExpr[]::new)), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), new Or(BCS.stream().toArray(ABoolExpr[]::new)).prime()));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + abstractTransition.getSource().getName(), model.getSource());
                ConcreteState c_ = new ConcreteState("c_" + abstractTransition.getTarget().getName(), model.getTarget());
                DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
                Kappa.put(c_, GREEN);
                return true;
            }
        }
        return false;
    }

    private boolean instantiateFromGreenToAny(AbstractTransition abstractTransition) {
        LinkedHashSet<ConcreteState> GCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(GREEN) && getAlpha().get(concreteState).equals(abstractTransition.getSource())).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!GCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(GCS.stream().toArray(ABoolExpr[]::new)), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getTarget().prime()));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + abstractTransition.getSource().getName(), model.getSource());
                ConcreteState c_ = new ConcreteState("c_" + abstractTransition.getTarget().getName(), model.getTarget());
                C.add(c_);
                DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
                Alpha.put(c_, abstractTransition.getTarget());
                Kappa.put(c_, GREEN);
                return true;
            }
        }
        return false;
    }

    private boolean instantiateFromBlueToBlue(AbstractTransition abstractTransition) {
        LinkedHashSet<ConcreteState> sourceBCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(BLUE) && getAlpha().get(concreteState).equals(abstractTransition.getSource())).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<ConcreteState> targetBCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(BLUE) && getAlpha().get(concreteState).equals(abstractTransition.getTarget())).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!sourceBCS.isEmpty() && !targetBCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(sourceBCS.stream().toArray(ABoolExpr[]::new)), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), new Or(targetBCS.stream().toArray(ABoolExpr[]::new)).prime()));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + abstractTransition.getSource().getName(), model.getSource());
                ConcreteState c_ = new ConcreteState("c_" + abstractTransition.getTarget().getName(), model.getTarget());
                DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
                return true;
            }
        }
        return false;
    }

    private boolean instantiateFromBlueToAny(AbstractTransition abstractTransition) {
        LinkedHashSet<ConcreteState> BCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(BLUE) && getAlpha().get(concreteState).equals(abstractTransition.getSource())).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!BCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(BCS.stream().toArray(ABoolExpr[]::new)), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getTarget().prime()));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + abstractTransition.getSource().getName(), model.getSource());
                ConcreteState c_ = new ConcreteState("c_" + abstractTransition.getTarget().getName(), model.getTarget());
                C.add(c_);
                Alpha.put(c_, abstractTransition.getTarget());
                Kappa.putIfAbsent(c_, BLUE);
                DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
                return true;
            }
        }
        return false;
    }

    private void instantiateFromWitnesses(AbstractTransition abstractTransition, Model model) {
        ConcreteState c = new ConcreteState("c_" + abstractTransition.getSource().getName(), model.getSource());
        ConcreteState c_ = new ConcreteState("c_" + abstractTransition.getTarget().getName(), model.getTarget());
        C.addAll(Arrays.asList(c, c_));
        Alpha.put(c, abstractTransition.getSource());
        Alpha.put(c_, abstractTransition.getTarget());
        Kappa.putIfAbsent(c, BLUE);
        Kappa.putIfAbsent(c_, BLUE);
        DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
    }

    public Machine getMachine() {
        return machine;
    }

    public LinkedHashSet<AbstractState> getA() {
        return A;
    }

    public IEventsOrderingFunction getEventsOrderingFunction() {
        return eventsOrderingFunction;
    }

    public IAbstractStatesOrderingFunction getAbstractStatesOrderingFunction() {
        return abstractStatesOrderingFunction;
    }

    public LinkedHashSet<AbstractState> getQ0() {
        return Q0;
    }

    public LinkedHashSet<AbstractState> getQ() {
        return Q;
    }

    public LinkedHashSet<AbstractTransition> getDelta() {
        return Delta;
    }

    public LinkedHashSet<AbstractTransition> getDeltaMinus() {
        return DeltaMinus;
    }

    public LinkedHashSet<AbstractTransition> getDeltaPlus() {
        return DeltaPlus;
    }

    public LinkedHashSet<ConcreteState> getC0() {
        return C0;
    }

    public LinkedHashSet<ConcreteState> getC() {
        return C;
    }

    public LinkedHashMap<ConcreteState, AbstractState> getAlpha() {
        return Alpha;
    }

    public LinkedHashMap<ConcreteState, EConcreteStateColor> getKappa() {
        return Kappa;
    }

    public LinkedHashSet<ConcreteTransition> getDeltaC() {
        return DeltaC;
    }

}
