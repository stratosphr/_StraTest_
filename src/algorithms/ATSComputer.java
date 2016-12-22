package algorithms;

import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ConcreteTransitionSystem;
import algorithms.outputs.EventSystem;
import algorithms.outputs.TriModalTransitionSystem;
import algorithms.utilities.EConcreteStateColor;
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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static algorithms.utilities.EConcreteStateColor.BLUE;
import static algorithms.utilities.EConcreteStateColor.GREEN;
import static com.microsoft.z3.Status.SATISFIABLE;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:36
 */
public class ATSComputer implements IComputer<ApproximatedTransitionSystem> {

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

    public ATSComputer(Machine machine, LinkedHashSet<AbstractState> A) {
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
    }

    @Override
    public ApproximatedTransitionSystem compute() {
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
            for (AbstractState q_ : getA()) {
                for (Event e : getMachine().getEvents()) {
                    z3.setCode(new And(getMachine().getInvariant(), getMachine().getInvariant().prime(), q, e.getSubstitution().getPrd(getMachine()), q_.prime()));
                    if (z3.checkSAT() == SATISFIABLE) {
                        Model model = z3.getModel();
                        getDelta().add(new AbstractTransition(q, e, q_));
                        instantiateFromGreenToBlue(new AbstractTransition(q, e, q_));
                        instantiateFromGreenToAny(new AbstractTransition(q, e, q_));
                        instantiateFromBlueToBlue(new AbstractTransition(q, e, q_));
                        instantiateFromBlueToAny(new AbstractTransition(q, e, q_));
                        instantiateFromWitnesses(new AbstractTransition(q, e, q_), model);
                        if (!Q.contains(q_)) {
                            RQ.add(q_);
                        }
                    }
                }
            }
        }
    }

    private void instantiateFromWitnesses(AbstractTransition abstractTransition, Model model) {
        ConcreteState c = new ConcreteState("c_" + abstractTransition.getSource().getName(), model.getSource());
        ConcreteState c_ = new ConcreteState("c_" + abstractTransition.getTarget().getName(), model.getTarget());
        C.addAll(Arrays.asList(c, c_));
        Alpha.put(c, abstractTransition.getSource());
        Alpha.put(c_, abstractTransition.getTarget());
        Kappa.put(c, BLUE);
        Kappa.put(c_, BLUE);
        DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
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
                System.out.println(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
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
                System.out.println(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
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
                System.out.println(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
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
                Kappa.put(c_, GREEN);
                DeltaC.add(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
                System.out.println(new ConcreteTransition(c, abstractTransition.getEvent(), c_));
                return true;
            }
        }
        return false;
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
