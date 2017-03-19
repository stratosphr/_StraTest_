package algorithms.statistics;

import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.utilities.ReachableAbstractPartComputer;
import algorithms.utilities.ReachableConcretePartComputer;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import utilities.AFormatter;
import utilities.sets.Tuple;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 18/03/17.
 * Time : 12:00
 */
public final class ATSComparator extends AFormatter {

    public ATSComparator(ApproximatedTransitionSystem ats1, ApproximatedTransitionSystem ats2) {
        Tuple<LinkedHashSet<AbstractState>, LinkedHashSet<AbstractTransition>> ats1ReachableAbstractPart = new ReachableAbstractPartComputer(ats1.getConcreteTransitionSystem().getC0(), ats1.getConcreteTransitionSystem().getDeltaC(), ats1.getConcreteTransitionSystem().getAlpha()).compute().getResult();
        Tuple<LinkedHashSet<AbstractState>, LinkedHashSet<AbstractTransition>> ats2ReachableAbstractPart = new ReachableAbstractPartComputer(ats2.getConcreteTransitionSystem().getC0(), ats2.getConcreteTransitionSystem().getDeltaC(), ats1.getConcreteTransitionSystem().getAlpha()).compute().getResult();
        Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> ats1ReachableConcretePart = new ReachableConcretePartComputer(ats1.getConcreteTransitionSystem().getC0(), ats1.getConcreteTransitionSystem().getDeltaC()).compute().getResult();
        Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> ats2ReachableConcretePart = new ReachableConcretePartComputer(ats2.getConcreteTransitionSystem().getC0(), ats2.getConcreteTransitionSystem().getDeltaC()).compute().getResult();
    }

    @Override
    public String toString() {
        String formatted = "";
        return formatted;
    }

}
