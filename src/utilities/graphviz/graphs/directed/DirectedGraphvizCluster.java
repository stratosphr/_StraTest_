package utilities.graphviz.graphs.directed;

import utilities.graphviz.graphs.AGraphvizCluster;
import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.visitors.GraphvizFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:57
 */
public final class DirectedGraphvizCluster extends AGraphvizCluster<DirectedGraphvizTransition> {

    public DirectedGraphvizCluster(String name, LinkedHashSet<AGraphvizNode> states, LinkedHashSet<DirectedGraphvizTransition> transitions) {
        super(name, states, transitions);
    }

    @Override
    public String accept(GraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
