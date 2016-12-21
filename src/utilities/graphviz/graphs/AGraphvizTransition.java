package utilities.graphviz.graphs;

import utilities.graphviz.AGraphvizObject;
import utilities.graphviz.graphs.parameters.GraphvizParameter;

import java.util.Collections;
import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:58
 */
public abstract class AGraphvizTransition extends AGraphvizObject {

    private final AGraphvizNode source;
    private final AGraphvizNode target;
    private final List<GraphvizParameter> parameters;

    public AGraphvizTransition(AGraphvizNode source, AGraphvizNode target) {
        this(source, target, Collections.emptyList());
    }

    public AGraphvizTransition(AGraphvizNode source, AGraphvizNode target, List<GraphvizParameter> parameters) {
        this.source = source;
        this.target = target;
        this.parameters = parameters;
    }

    public AGraphvizNode getSource() {
        return source;
    }

    public AGraphvizNode getTarget() {
        return target;
    }

    public List<GraphvizParameter> getParameters() {
        return parameters;
    }

}
