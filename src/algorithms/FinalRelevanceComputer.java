package algorithms;

import algorithms.heuristics.DefaultAbstractStatesOrderingFunction;
import algorithms.heuristics.DefaultEventsOrderingFunction;
import algorithms.heuristics.IRelevanceCheckingFunction;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.utilities.ReachableAbstractPartComputer;
import algorithms.utilities.ReachableConcretePartComputer;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.bool.And;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import solvers.z3.Model;
import solvers.z3.Z3;
import utilities.sets.Tuple;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static algorithms.heuristics.EConcreteStateColor.GREEN;
import static com.microsoft.z3.Status.SATISFIABLE;
import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 10/05/17.
 * Time : 12:25
 */
public class FinalRelevanceComputer extends AComputer<ApproximatedTransitionSystem> {

    private final Machine machine;
    private final ApproximatedTransitionSystem ats;
    private final IRelevanceCheckingFunction relevanceCheckingFunction;
    private final Z3 z3;

    public FinalRelevanceComputer(Machine machine, ApproximatedTransitionSystem ats, IRelevanceCheckingFunction relevanceCheckingFunction) {
        this.machine = machine;
        this.ats = ats.clone();
        this.relevanceCheckingFunction = relevanceCheckingFunction;
        this.z3 = new Z3();
    }

    @Override
    protected ApproximatedTransitionSystem compute_() {
        LinkedHashSet<ConcreteState> RCS = new ReachableConcretePartComputer(ats.getCTS().getC0(), ats.getCTS().getDeltaC()).compute().getResult().getFirst();
        while (!RCS.isEmpty()) {
            ConcreteState c = RCS.iterator().next();
            RCS.remove(c);
            AbstractState q = ats.getCTS().getAlpha().get(c);
            System.out.println(RCS.size());
            for (AbstractState q_ : new DefaultAbstractStatesOrderingFunction().apply(new Tuple<>(q, ats.get3MTS().getQ()))) {
                for (Event e : new DefaultEventsOrderingFunction().apply(machine.getEvents())) {
                    if (ats.get3MTS().getDelta().contains(new AbstractTransition(q, e, q_))) {
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
                            if (!ats.getCTS().getC().contains(c_)) {
                                c_.setName("c_" + q_.getName() + "_" + ats.getCTS().getC().size());
                            } else {
                                c_.setName(new ArrayList<>(ats.getCTS().getC()).get(new ArrayList<>(ats.getCTS().getC()).indexOf(c_)).getName());
                            }
                            if (!ats.getCTS().getDeltaC().contains(new ConcreteTransition(c, e, c_)) && relevanceCheckingFunction.apply(new Tuple<>(c, c_))) {
                                RCS.add(c_);
                            }
                            /*if (relevanceCheckingFunction.apply(new Tuple<>(c, c_))) {
                                RCS.add(c_);
                            }*/
                            ats.getCTS().getAlpha().put(c_, q_);
                            ats.getCTS().getKappa().put(c_, GREEN);
                            ats.getCTS().getC().add(c_);
                            ats.getCTS().getDeltaC().add(new ConcreteTransition(c, e, c_));
                        }
                    }
                }
            }
        }
        Tuple<LinkedHashSet<AbstractState>, LinkedHashSet<AbstractTransition>> reachablePart = new ReachableAbstractPartComputer(ats.getCTS().getC0(), ats.getCTS().getDeltaC(), ats.getCTS().getAlpha()).compute().getResult();
        System.out.println(ats.get3MTS().getDelta().stream().filter(abstractTransition -> !reachablePart.getSecond().contains(abstractTransition)).map(abstractTransition -> abstractTransition.getSource().getName() + " " + abstractTransition.getEvent().getName() + " " + abstractTransition.getTarget().getName()).collect(Collectors.joining(NEW_LINE)));
        return ats;
    }

}
