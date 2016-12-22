package algorithms.outputs;

import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:42
 */
public final class TriModalTransitionSystem {

    private final LinkedHashSet<AbstractState> Q0;
    private final LinkedHashSet<AbstractState> Q;
    private final LinkedHashSet<AbstractTransition> Delta;
    private final LinkedHashSet<AbstractTransition> DeltaMinus;
    private final LinkedHashSet<AbstractTransition> DeltaPlus;

    public TriModalTransitionSystem(LinkedHashSet<AbstractState> Q0, LinkedHashSet<AbstractState> Q, LinkedHashSet<AbstractTransition> Delta, LinkedHashSet<AbstractTransition> DeltaMinus, LinkedHashSet<AbstractTransition> DeltaPlus) {
        this.Q0 = Q0;
        this.Q = Q;
        this.Delta = Delta;
        this.DeltaMinus = DeltaMinus;
        this.DeltaPlus = DeltaPlus;
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

}
