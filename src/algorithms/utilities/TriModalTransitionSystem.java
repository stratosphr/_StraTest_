package algorithms.utilities;

import algorithms.visitors.DOTFormatter;
import algorithms.visitors.IDOTFormatterVisitable;
import graphs.eventb.AbstractState;
import graphs.eventb.AbstractTransition;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 19:49
 */
public final class TriModalTransitionSystem implements IDOTFormatterVisitable {

    private final LinkedHashSet<AbstractState> Q;
    private final LinkedHashSet<AbstractState> Q0;
    private final LinkedHashSet<AbstractTransition> Delta;
    private final LinkedHashSet<AbstractTransition> DeltaPlus;
    private final LinkedHashSet<AbstractTransition> DeltaMinus;

    public TriModalTransitionSystem(LinkedHashSet<AbstractState> Q, LinkedHashSet<AbstractState> Q0, LinkedHashSet<AbstractTransition> Delta, LinkedHashSet<AbstractTransition> DeltaPlus, LinkedHashSet<AbstractTransition> DeltaMinus) {
        this.Q = Q;
        this.Q0 = Q0;
        this.Delta = Delta;
        this.DeltaPlus = DeltaPlus;
        this.DeltaMinus = DeltaMinus;
    }

    @Override
    public String accept(DOTFormatter visitor) {
        return visitor.visit(this);
    }

    public LinkedHashSet<AbstractState> getQ() {
        return Q;
    }

    public LinkedHashSet<AbstractState> getQ0() {
        return Q0;
    }

    public LinkedHashSet<AbstractTransition> getDelta() {
        return Delta;
    }

    public LinkedHashSet<AbstractTransition> getDeltaPlus() {
        return DeltaPlus;
    }

    public LinkedHashSet<AbstractTransition> getDeltaMinus() {
        return DeltaMinus;
    }

}
