package utilities.graphviz.graphs.directed;

import utilities.graphviz.graphs.AGraphvizGraph;
import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.graphs.parameters.AGraphvizParameter;
import utilities.graphviz.visitors.GraphvizFormatter;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:55
 */
public final class DirectedGraphvizGraph extends AGraphvizGraph<DirectedGraphvizTransition> {

    public DirectedGraphvizGraph(LinkedHashSet<AGraphvizNode> states, LinkedHashSet<DirectedGraphvizTransition> transitions) {
        super(states, transitions);
    }

    public DirectedGraphvizGraph(LinkedHashSet<AGraphvizNode> states, LinkedHashSet<DirectedGraphvizTransition> transitions, List<AGraphvizParameter> parameters) {
        super(states, transitions, parameters);
    }

    @Override
    public String accept(GraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
