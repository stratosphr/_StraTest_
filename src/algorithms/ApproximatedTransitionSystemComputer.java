package algorithms;

import algorithms.utilities.*;
import com.microsoft.z3.Status;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Or;
import graphs.eventb.AbstractState;
import graphs.eventb.AbstractTransition;
import graphs.eventb.ConcreteState;
import graphs.eventb.ConcreteTransition;
import solvers.z3.Model;
import solvers.z3.Z3;
import utilities.Tuple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 14:52
 */
public final class ApproximatedTransitionSystemComputer {

    private final Machine machine;
    private final LinkedHashSet<AbstractState> abstractStates;

    private final LinkedHashSet<AbstractState> Q;
    private final LinkedHashSet<AbstractState> Q0;
    private LinkedHashSet<ConcreteState> C;
    private final LinkedHashSet<ConcreteState> C0;
    private final LinkedHashSet<AbstractTransition> Delta;
    private final LinkedHashSet<AbstractTransition> DeltaPlus;
    private final LinkedHashSet<AbstractTransition> DeltaMinus;
    private final LinkedHashMap<ConcreteState, AbstractState> Alpha;
    private final LinkedHashMap<ConcreteState, EStateColor> Kappa;
    private final LinkedHashMap<AbstractTransition, Tuple<Integer, Set<Integer>>> ThetaA;
    private final LinkedHashMap<ConcreteTransition, Tuple<Integer, Set<Integer>>> ThetaC;
    private final LinkedHashSet<ConcreteTransition> DeltaC;
    private final LinkedHashSet<AbstractState> RQ;
    private final LinkedHashSet<ConcreteState> RC;
    private LinkedHashSet<ConcreteState> GC;
    private LinkedHashSet<ConcreteState> BC;
    private final Z3 z3;

    public ApproximatedTransitionSystemComputer(Machine machine, LinkedHashSet<AbstractState> abstractStates) {
        this.machine = machine;
        this.abstractStates = abstractStates;
        Q = new LinkedHashSet<>();
        Q0 = new LinkedHashSet<>();
        C = new LinkedHashSet<>();
        C0 = new LinkedHashSet<>();
        Delta = new LinkedHashSet<>();
        DeltaPlus = new LinkedHashSet<>();
        DeltaMinus = new LinkedHashSet<>();
        Alpha = new LinkedHashMap<>();
        Kappa = new LinkedHashMap<>();
        ThetaA = new LinkedHashMap<>();
        ThetaC = new LinkedHashMap<>();
        DeltaC = new LinkedHashSet<>();
        RQ = new LinkedHashSet<>();
        RC = new LinkedHashSet<>();
        GC = new LinkedHashSet<>();
        BC = new LinkedHashSet<>();
        z3 = new Z3();
    }

    public ApproximatedTransitionSystem computeATS() {
        // Time measurement
        long startTime;
        long endTime;
        double computationTime;
        startTime = System.nanoTime();
        // Step 1: Computation of one concrete instance of each initial abstract state
        computeInitialAbstractStatesInstances();
        C = new LinkedHashSet<>(C0);
        RQ.addAll(Q0);
        instantiateAbstractTransitions();
        endTime = System.nanoTime();
        EventSystem eventSystem = new EventSystem(machine.getAssignables(), machine.getInvariant(), machine.getInitialisation(), machine.getEvents());
        TriModalTransitionSystem triModalTransitionSystem = new TriModalTransitionSystem(Q, Q0, Delta, DeltaPlus, DeltaMinus);
        EventSystemUnderApproximation eventSystemUnderApproximation = new EventSystemUnderApproximation(C, C0, Alpha, Kappa, DeltaC);
        return new ApproximatedTransitionSystem(eventSystem, triModalTransitionSystem, eventSystemUnderApproximation);
    }

    private void computeInitialAbstractStatesInstances() {
        for (AbstractState q : abstractStates) {
            z3.setCode(new And(machine.getInvariant(), machine.getInvariant().prime(), machine.getInitialisation().getPrd(machine), q.prime()));
            if (z3.checkSAT() == Status.SATISFIABLE) {
                ConcreteState c = new ConcreteState("c_" + q.getName(), z3.getModel().getTarget());
                Q0.add(q);
                C0.add(c);
                Alpha.put(c, q);
                Kappa.put(c, EStateColor.GREEN);
                /*if (isUseRelevanceChecker()) {
                    RC.add(c);
                }*/
            }
        }
    }

    private void instantiateAbstractTransitions() {
        int loop = -1;
        RQ.addAll(Q0);
        while (!RQ.isEmpty()) {
            ++loop;
            AbstractState q = RQ.iterator().next();
            RQ.remove(q);
            Q.add(q);
            for (AbstractState q_ : orderStates(abstractStates, q)) {
                for (Event e : orderEvents(machine.getEvents())) {
                    Tuple<Boolean, Model> nc = ModalityChecker.isMayWithModel(new AbstractTransition(q, e, q_), machine);
                    if (nc.getFirst()) {
                        /*if (new ModalityChecker(machine).isMustMinus(new AbstractTransition(q, e, q_))) {
                            DeltaMinus.add(new AbstractTransition(q, e, q_));
                        }
                        if (new ModalityChecker(machine).isMustPlus(new AbstractTransition(q, e, q_))) {
                            DeltaPlus.add(new AbstractTransition(q, e, q_));
                        }*/
                        Model model;
                        ConcreteState c;
                        ConcreteState c_;
                        Delta.add(new AbstractTransition(q, e, q_));
                        ThetaA.put(new AbstractTransition(q, e, q_), new Tuple<>(loop, new LinkedHashSet<>()));
                        GC = Alpha.keySet().stream().filter(concreteState -> Alpha.get(concreteState).equals(q) && Kappa.get(concreteState).equals(EStateColor.GREEN)).collect(Collectors.toCollection(LinkedHashSet::new));
                        BC = Alpha.keySet().stream().filter(concreteState -> Alpha.get(concreteState).equals(q_) && Kappa.get(concreteState).equals(EStateColor.BLUE)).collect(Collectors.toCollection(LinkedHashSet::new));
                        z3.setCode(new And(
                                machine.getInvariant(),
                                machine.getInvariant().prime(),
                                new Or(GC.stream().toArray(ABoolExpr[]::new)),
                                e.getSubstitution().getPrd(machine),
                                new Or(BC.stream().toArray(ABoolExpr[]::new)).prime()
                        ));
                        Status greenToBlueSAT = z3.checkSAT();
                        if (greenToBlueSAT == Status.SATISFIABLE) {
                            model = z3.getModel();
                            c = new ConcreteState("c_" + q.getName(), model.getSource());
                            c_ = new ConcreteState("c_" + q_.getName(), model.getTarget());
                            /* ***************** */
                            Alpha.put(c_, q_);
                            Kappa.put(c_, EStateColor.GREEN);
                            C.addAll(Arrays.asList(c, c_));
                            if (DeltaC.add(new ConcreteTransition(c, e, c_))) {
                                ThetaC.put(new ConcreteTransition(c, e, c_), new Tuple<>(loop, new LinkedHashSet<>(Collections.singletonList(1))));
                            } else {
                                ThetaC.get(new ConcreteTransition(c, e, c_)).getSecond().add(1);
                            }
                            ThetaA.get(new AbstractTransition(q, e, q_)).getSecond().add(1);
                            /* ***************** */
                        } else {
                            z3.setCode(new And(
                                    machine.getInvariant(),
                                    machine.getInvariant().prime(),
                                    new Or(GC.stream().toArray(ABoolExpr[]::new)),
                                    e.getSubstitution().getPrd(machine),
                                    q_.getExpression().prime()
                            ));
                            Status greenToGreenSAT = z3.checkSAT();
                            if (greenToGreenSAT == Status.SATISFIABLE) {
                                model = z3.getModel();
                                c = new ConcreteState("c_" + q.getName(), model.getSource());
                                c_ = new ConcreteState("c_" + q_.getName(), model.getTarget());
                                /* ***************** */
                                Alpha.put(c, q);
                                Alpha.put(c_, q_);
                                Kappa.put(c_, EStateColor.GREEN);
                                C.addAll(Arrays.asList(c, c_));
                                if (DeltaC.add(new ConcreteTransition(c, e, c_))) {
                                    ThetaC.put(new ConcreteTransition(c, e, c_), new Tuple<>(loop, new LinkedHashSet<>(Collections.singletonList(2))));
                                } else {
                                    ThetaC.get(new ConcreteTransition(c, e, c_)).getSecond().add(2);
                                }
                                ThetaA.get(new AbstractTransition(q, e, q_)).getSecond().add(2);
                                /* ***************** */
                            } else {
                                BC = Alpha.keySet().stream().filter(concreteState -> Alpha.get(concreteState).equals(q) && Kappa.get(concreteState).equals(EStateColor.BLUE)).collect(Collectors.toCollection(LinkedHashSet::new));
                                z3.setCode(new And(
                                        machine.getInvariant(),
                                        machine.getInvariant().prime(),
                                        new Or(BC.stream().toArray(ABoolExpr[]::new)),
                                        e.getSubstitution().getPrd(machine),
                                        q_.getExpression().prime()
                                ));
                                Status blueToBlueSAT = z3.checkSAT();
                                if (blueToBlueSAT == Status.SATISFIABLE) {
                                    model = z3.getModel();
                                    c = new ConcreteState("c_" + q.getName(), model.getSource());
                                    c_ = new ConcreteState("c_" + q_.getName(), model.getTarget());
                                    /* ***************** */
                                    Alpha.put(c_, q_);
                                    Kappa.putIfAbsent(c_, EStateColor.BLUE);
                                    C.add(c_);
                                    if (DeltaC.add(new ConcreteTransition(c, e, c_))) {
                                        ThetaC.put(new ConcreteTransition(c, e, c_), new Tuple<>(loop, new LinkedHashSet<>(Collections.singletonList(3))));
                                        //updateConnectedGraphs(connectedGraphs, new ConcreteTransition(c, e, c_));
                                    } else {
                                        ThetaC.get(new ConcreteTransition(c, e, c_)).getSecond().add(3);
                                    }
                                    ThetaA.get(new AbstractTransition(q, e, q_)).getSecond().add(3);
                                    /* ***************** */
                                } else {
                                    model = nc.getSecond();
                                    c = new ConcreteState("c_" + q.getName(), model.getSource());
                                    c_ = new ConcreteState("c_" + q_.getName(), model.getTarget());
                                    /* ***************** */
                                    Alpha.put(c, q);
                                    Alpha.put(c_, q_);
                                    Kappa.putIfAbsent(c, EStateColor.BLUE);
                                    Kappa.putIfAbsent(c_, EStateColor.BLUE);
                                    C.addAll(Arrays.asList(c, c_));
                                    if (DeltaC.add(new ConcreteTransition(c, e, c_))) {
                                        ThetaC.put(new ConcreteTransition(c, e, c_), new Tuple<>(loop, new LinkedHashSet<>(Collections.singletonList(4))));
                                        //updateConnectedGraphs(connectedGraphs, new ConcreteTransition(c, e, c_));
                                    } else {
                                        ThetaC.get(new ConcreteTransition(c, e, c_)).getSecond().add(4);
                                    }
                                    ThetaA.get(new AbstractTransition(q, e, q_)).getSecond().add(4);
                                    /* ***************** */
                                }
                            }
                        }
                        if (!Q.contains(q_)) {
                            RQ.add(q_);
                        }
                    }
                }
            }
        }
    }

    private List<AbstractState> orderStates(LinkedHashSet<AbstractState> abstractStates, AbstractState q) {
        LinkedHashSet<AbstractState> sortedAbstractStatesSet = new LinkedHashSet<>();
        sortedAbstractStatesSet.add(q);
        sortedAbstractStatesSet.addAll(abstractStates);
        return new ArrayList<>(sortedAbstractStatesSet);
    }

    private List<Event> orderEvents(LinkedHashSet<Event> events) {
        List<Event> orderedEvents = new ArrayList<>(events);
        Collections.sort(orderedEvents);
        return orderedEvents;
    }

}
