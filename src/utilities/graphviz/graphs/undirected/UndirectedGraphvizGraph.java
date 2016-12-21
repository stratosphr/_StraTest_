package utilities.graphviz.graphs.undirected;

import utilities.graphviz.graphs.AGraphvizGraph;
import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.visitors.GraphvizFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:55
 */
public final class UndirectedGraphvizGraph extends AGraphvizGraph<UndirectedGraphvizTransition> {

    public UndirectedGraphvizGraph(LinkedHashSet<AGraphvizNode> states, LinkedHashSet<UndirectedGraphvizTransition> transitions) {
        super(states, transitions);
    }

    @Override
    public String accept(GraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
