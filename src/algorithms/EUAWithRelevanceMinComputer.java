package algorithms;

import algorithms.heuristics.DefaultAbstractStatesOrderingFunction;
import algorithms.heuristics.DefaultEventsOrderingFunction;
import algorithms.heuristics.EConcreteStateColor;
import algorithms.heuristics.IRelevanceCheckingFunction;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ConcreteTransitionSystem;
import algorithms.outputs.EventSystem;
import algorithms.outputs.TriModalTransitionSystem;
import algorithms.utilities.ReachableAbstractPartComputer;
import algorithms.utilities.ReachableConcretePartComputer;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.arith.Int;
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

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.heuristics.EConcreteStateColor.BLUE;
import static algorithms.heuristics.EConcreteStateColor.GREEN;
import static com.microsoft.z3.Status.SATISFIABLE;
import static com.microsoft.z3.Status.UNSATISFIABLE;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:36
 */
public final class EUAWithRelevanceMinComputer extends AComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final LinkedHashSet<AbstractState> A;
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
    private final IRelevanceCheckingFunction relevanceCheckingFunction;

    public EUAWithRelevanceMinComputer(Machine machine, LinkedHashSet<AbstractState> A, IRelevanceCheckingFunction relevanceCheckingFunction) {
        this.machine = machine;
        this.A = A;
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
        this.relevanceCheckingFunction = relevanceCheckingFunction;
    }

    @Override
    public ApproximatedTransitionSystem compute_() {
        step1();
        step2();
        step3();
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
        Map<Event, LinkedHashSet<AbstractState>> E_ = new LinkedHashMap<>();
        Map<AbstractState, LinkedHashSet<Event>> _E = new LinkedHashMap<>();
        for (Event e : machine.getEvents()) {
            E_.put(e, new LinkedHashSet<>());
            for (AbstractState q : A) {
                z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), e.getSubstitution().getPrd(machine), q.prime()));
                if (z3.checkSAT() == SATISFIABLE) {
                    E_.get(e).add(q);
                }
                _E.putIfAbsent(q, new LinkedHashSet<>());
                z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), q, e.getSubstitution().getPrd(machine)));
                if (z3.checkSAT() == SATISFIABLE) {
                    _E.get(q).add(e);
                }
            }
        }

        boolean added;
        RQ.addAll(getQ0());
        do {
            added = false;
            RQ.addAll(Q);
            while (!RQ.isEmpty()) {
                AbstractState q = RQ.iterator().next();
                RQ.remove(q);
                Q.add(q);
                for (Event e : _E.get(q)) {
                    LinkedHashSet<ConcreteState> GCS = C.stream().filter(concreteState -> Alpha.get(concreteState).equals(q) && Kappa.get(concreteState).equals(GREEN)).collect(Collectors.toCollection(LinkedHashSet::new));
                    z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), new Or(GCS.toArray(new ConcreteState[GCS.size()])), e.getSubstitution().getPrd(getMachine()), new Or(E_.get(e).stream().filter(abstractState -> !Delta.contains(new AbstractTransition(q, e, abstractState))).map(abstractState -> new And(abstractState, new Equals(new IntVariable("q!i"), new Int(new ArrayList<>(getA()).indexOf(abstractState))))).toArray(ABoolExpr[]::new)).prime()));
                    if (z3.checkSAT() == SATISFIABLE) {
                        added = true;
                        Model model = z3.getModel();
                        int q_Index = model.getTarget().remove(new IntVariable("q!i")).getValue();
                        AbstractState q_ = new ArrayList<>(getA()).get(q_Index);
                        ConcreteState c = new ConcreteState("c_" + q.getName(), model.getSource());
                        ConcreteState c_ = new ConcreteState("c_" + q_.getName(), model.getTarget());
                        Delta.add(new AbstractTransition(q, e, q_));
                        Alpha.put(c, q);
                        Alpha.put(c_, q_);
                        Kappa.put(c_, GREEN);
                        C.addAll(Arrays.asList(c, c_));
                        DeltaC.add(new ConcreteTransition(c, e, c_));
                        RQ.add(q_);
                    }
                }
            }
        } while (added);
        RQ.clear();
        RQ.addAll(Q);
        while (!RQ.isEmpty()) {
            AbstractState q = RQ.iterator().next();
            RQ.remove(q);
            Q.add(q);
            for (Event e : _E.get(q)) {
                for (AbstractState q_ : E_.get(e)) {
                    if (!Delta.contains(new AbstractTransition(q, e, q_))) {
                        z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), q, e.getSubstitution().getPrd(machine), q_.prime()));
                        if (z3.checkSAT() == SATISFIABLE) {
                            AbstractTransition abstractTransition = new AbstractTransition(q, e, q_);
                            getDelta().add(abstractTransition);
                            if (!Q.contains(q_)) {
                                RQ.add(q_);
                            }
                        }
                    }
                }
            }
        }
    }

    private void step3() {
        LinkedHashSet<ConcreteState> RCS = new ReachableConcretePartComputer(C0, DeltaC).compute().getResult().getFirst();
        ConcreteState cMin = RCS.iterator().next();
        while (!RCS.isEmpty()) {
            ConcreteState c = RCS.iterator().next();
            RCS.remove(c);
            AbstractState q = Alpha.get(c);
            for (AbstractState q_ : new DefaultAbstractStatesOrderingFunction().apply(new Tuple<>(q, A))) {
                for (Event e : new DefaultEventsOrderingFunction().apply(machine.getEvents())) {
                    if (Delta.contains(new AbstractTransition(q, e, q_))) {
                        z3.setCode(new And(
                                machine.getInvariant(),
                                machine.getInvariant().prime(),
                                c,
                                e.getSubstitution().getPrd(machine),
                                //new Not(new Or(DeltaC.stream().filter(concreteTransition -> concreteTransition.getSource().equals(c) && concreteTransition.getEvent().equals(e)).map(ATransition::getTarget).toArray(ConcreteState[]::new))),
                                q_.prime()
                        ));
                        if (z3.checkSAT() == SATISFIABLE) {
                            Model model = z3.getModel();
                            ConcreteState c_ = new ConcreteState("c_" + q_.getName(), model.getTarget());
                            /*if (relevanceCheckingFunction.apply(new Tuple<>(cMin, c_))) {
                                if (!C.contains(c_)) {
                                    RCS.add(c_);
                                }
                                cMin = c_;
                            }*/
                            if (!C.contains(c_) && relevanceCheckingFunction.apply(new Tuple<>(cMin, c_))) {
                                RCS.add(c_);
                                cMin = c_;
                            }
                            Alpha.put(c_, q_);
                            Kappa.put(c_, GREEN);
                            C.add(c_);
                            DeltaC.add(new ConcreteTransition(c, e, c_));
                            /*if (new ReachableAbstractPartComputer(C0, DeltaC, Alpha).compute().getResult().getSecond().containsAll(Delta)) {
                                System.out.println("here");
                                LinkedHashSet<AbstractTransition> tmpDelta = new LinkedHashSet<>(Delta);
                                tmpDelta.removeAll(new ReachableAbstractPartComputer(C0, DeltaC, Alpha).compute().getResult().getSecond());
                                System.out.println(tmpDelta);
                                return;
                            } else {
                                System.out.println(c_);
                            }*/
                        }
                    }
                }
            }
        }
        LinkedHashSet<AbstractTransition> tmpDelta = new LinkedHashSet<>(Delta);
        tmpDelta.removeAll(new ReachableAbstractPartComputer(C0, DeltaC, Alpha).compute().getResult().getSecond());
        System.out.println(tmpDelta);
    }

    private boolean registerMustMinus(AbstractTransition abstractTransition) {
        z3.setCode(new And(getMachine().getInvariant().prime(), new Not(new Exists(new And(getMachine().getInvariant(), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getSource()), getMachine().getQuantifiedVariables().toArray(new QuantifiedVariable[0]))), abstractTransition.getTarget().getExpression().prime()));
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

    private boolean instantiateFromKnownToAny(AbstractTransition abstractTransition) {
        LinkedHashSet<ConcreteState> KCS = getC().stream().filter(concreteState -> getAlpha().get(concreteState).equals(abstractTransition.getSource())).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!KCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(KCS.toArray(new ABoolExpr[0])), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getTarget().prime()));
            if (z3.checkSAT() == SATISFIABLE) {
                Model model = z3.getModel();
                ConcreteState c = new ConcreteState("c_" + abstractTransition.getSource().getName(), model.getSource());
                ConcreteState c_ = new ConcreteState("c_" + abstractTransition.getTarget().getName(), model.getTarget());
                C.add(c_);
                Alpha.put(c_, abstractTransition.getTarget());
                DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
                return true;
            }
        }
        return false;
    }

    public boolean instantiateFromGreenToBlue(AbstractTransition abstractTransition) {
        LinkedHashSet<ConcreteState> GCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(GREEN) && getAlpha().get(concreteState).equals(abstractTransition.getSource())).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<ConcreteState> BCS = getC().stream().filter(concreteState -> getKappa().get(concreteState).equals(BLUE) && getAlpha().get(concreteState).equals(abstractTransition.getTarget())).collect(Collectors.toCollection(LinkedHashSet::new));
        if (!GCS.isEmpty() && !BCS.isEmpty()) {
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(GCS.toArray(new ABoolExpr[0])), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), new Or(BCS.toArray(new ABoolExpr[0])).prime()));
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
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(GCS.toArray(new ABoolExpr[0])), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getTarget().prime()));
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
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(sourceBCS.toArray(new ABoolExpr[0])), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), new Or(targetBCS.toArray(new ABoolExpr[0])).prime()));
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
            z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), new Or(BCS.toArray(new ABoolExpr[0])), abstractTransition.getEvent().getSubstitution().getPrd(getMachine()), abstractTransition.getTarget().prime()));
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
        if (!Kappa.containsKey(c)) {
            Kappa.put(c, BLUE);
        }
        if (!Kappa.containsKey(c_) || Kappa.get(c) == GREEN) {
            Kappa.put(c_, Kappa.get(c));
        }
        /*if (Kappa.containsKey(c) && Kappa.get(c).equals(GREEN)) {
            Kappa.put(c_, GREEN);
        } else {
            Kappa.putIfAbsent(c, BLUE);
            Kappa.putIfAbsent(c_, BLUE);
        }*/
        DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
    }

    public Machine getMachine() {
        return machine;
    }

    public LinkedHashSet<AbstractState> getA() {
        return A;
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
