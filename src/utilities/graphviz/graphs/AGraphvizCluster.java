package utilities.graphviz.graphs;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:57
 */
public abstract class AGraphvizCluster<Transition extends AGraphvizTransition> extends AGraphvizNode {

    private final LinkedHashSet<AGraphvizNode> states;
    private final LinkedHashSet<Transition> transitions;

    public AGraphvizCluster(String name, LinkedHashSet<AGraphvizNode> states, LinkedHashSet<Transition> transitions) {
        super(name);
        this.states = states;
        this.transitions = transitions;
    }

    public LinkedHashSet<AGraphvizNode> getStates() {
        return states;
    }

    public LinkedHashSet<Transition> getTransitions() {
        return transitions;
    }

}
