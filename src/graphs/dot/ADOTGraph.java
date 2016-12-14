package graphs.dot;

import algorithms.visitors.DOTFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:10
 */
public abstract class ADOTGraph extends ADOTObject {

    private final LinkedHashSet<ADOTState> states;
    private final LinkedHashSet<? extends ADOTTransition> transitions;

    public ADOTGraph(LinkedHashSet<ADOTState> states, LinkedHashSet<? extends ADOTTransition> transitions) {
        this.states = states;
        this.transitions = transitions;
    }

    public LinkedHashSet<ADOTState> getStates() {
        return states;
    }

    public LinkedHashSet<? extends ADOTTransition> getTransitions() {
        return transitions;
    }

    @Override
    public final String toString() {
        return accept(new DOTFormatter());
    }

}
