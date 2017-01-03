package utilities.graphviz.graphs;

import utilities.graphviz.graphs.parameters.AGraphvizParameter;
import utilities.graphviz.graphs.parameters.ANonGlobalGraphvizParameter;
import utilities.graphviz.visitors.AGraphvizFormatter;

import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:57
 */
public final class GraphvizNode extends AGraphvizNode {

    public GraphvizNode(String name) {
        super(name);
    }

    public GraphvizNode(String name, List<ANonGlobalGraphvizParameter> parameters) {
        super(name, parameters);
    }

    @Override
    public String accept(AGraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
