package utilities.graphviz.graphs.undirected;

import utilities.graphviz.graphs.AGraphvizCluster;
import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.visitors.AGraphvizFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:57
 */
public final class UndirectedGraphvizCluster extends AGraphvizCluster<UndirectedGraphvizTransition> {

    public UndirectedGraphvizCluster(String name, LinkedHashSet<AGraphvizNode> states, LinkedHashSet<UndirectedGraphvizTransition> transitions) {
        super(name, states, transitions);
    }

    @Override
    public String accept(AGraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
