package utilities.graphviz.graphs.undirected;

import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.graphs.AGraphvizTransition;
import utilities.graphviz.graphs.parameters.GraphvizParameter;
import utilities.graphviz.visitors.GraphvizFormatter;

import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:58
 */
public final class UndirectedGraphvizTransition extends AGraphvizTransition {

    public UndirectedGraphvizTransition(AGraphvizNode source, AGraphvizNode target) {
        super(source, target);
    }

    public UndirectedGraphvizTransition(AGraphvizNode source, AGraphvizNode target, List<GraphvizParameter> parameters) {
        super(source, target, parameters);
    }

    @Override
    public String accept(GraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
