package utilities.graphviz.graphs.parameters;

import utilities.graphviz.visitors.GraphvizFormatter;

import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 16:24
 */
public final class GlobalGraphvizParameter extends AGraphvizParameter {

    private final String scope;
    private final List<GraphvizParameter> parameters;

    public GlobalGraphvizParameter(String scope, List<GraphvizParameter> parameters) {
        this.scope = scope;
        this.parameters = parameters;
    }

    public String getScope() {
        return scope;
    }

    public List<GraphvizParameter> getParameters() {
        return parameters;
    }

    @Override
    public String accept(GraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
