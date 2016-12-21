package utilities.graphviz.graphs;

import utilities.graphviz.graphs.parameters.GraphvizParameter;
import utilities.graphviz.visitors.GraphvizFormatter;

import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:57
 */
public final class GraphvizNode extends AGraphvizNode {

    public GraphvizNode(String name) {
        super(name);
    }

    public GraphvizNode(String name, List<GraphvizParameter> parameters) {
        super(name, parameters);
    }

    @Override
    public String accept(GraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
