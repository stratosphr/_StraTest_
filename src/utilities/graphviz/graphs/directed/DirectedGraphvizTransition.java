package utilities.graphviz.graphs.directed;

import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.graphs.AGraphvizTransition;
import utilities.graphviz.graphs.parameters.ANonGlobalGraphvizParameter;
import utilities.graphviz.visitors.AGraphvizFormatter;

import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:58
 */
public final class DirectedGraphvizTransition extends AGraphvizTransition {

    public DirectedGraphvizTransition(AGraphvizNode source, AGraphvizNode target) {
        super(source, target);
    }

    public DirectedGraphvizTransition(AGraphvizNode source, AGraphvizNode target, List<ANonGlobalGraphvizParameter> parameters) {
        super(source, target, parameters);
    }

    @Override
    public String accept(AGraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
