package graphs.dot;

import algorithms.visitors.DOTFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:12
 */
public final class DOTCluster extends ADOTState {

    private final LinkedHashSet<ADOTState> states;
    private final LinkedHashSet<ADOTTransition> transitions;

    public DOTCluster(String name, LinkedHashSet<ADOTState> states, LinkedHashSet<ADOTTransition> transitions) {
        this(name, name, states, transitions);
    }

    public DOTCluster(String name, String label, LinkedHashSet<ADOTState> states, LinkedHashSet<ADOTTransition> transitions) {
        super(name, label);
        this.states = states;
        this.transitions = transitions;
    }

    @Override
    public String accept(DOTFormatter visitor) {
        return visitor.visit(this);
    }

    public LinkedHashSet<ADOTTransition> getTransitions() {
        return transitions;
    }

    public LinkedHashSet<ADOTState> getStates() {
        return states;
    }

}
