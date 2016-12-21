package utilities.graphviz.graphs;

import utilities.graphviz.AGraphvizObject;
import utilities.graphviz.graphs.parameters.AGraphvizParameter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:55
 */
public abstract class AGraphvizGraph<Transition extends AGraphvizTransition> extends AGraphvizObject {

    private final LinkedHashSet<AGraphvizNode> nodes;
    private final LinkedHashSet<Transition> transitions;
    private final List<AGraphvizParameter> parameters;

    public AGraphvizGraph(LinkedHashSet<AGraphvizNode> nodes, LinkedHashSet<Transition> transitions) {
        this(nodes, transitions, Collections.emptyList());
    }

    public AGraphvizGraph(LinkedHashSet<AGraphvizNode> nodes, LinkedHashSet<Transition> transitions, List<AGraphvizParameter> parameters) {
        this.nodes = nodes;
        this.transitions = transitions;
        this.parameters = parameters;
    }

    public LinkedHashSet<AGraphvizNode> getNodes() {
        return nodes;
    }

    public LinkedHashSet<Transition> getTransitions() {
        return transitions;
    }

    public List<AGraphvizParameter> getParameters() {
        return parameters;
    }

}
