package utilities.graphviz.graphs.parameters;

import utilities.graphviz.visitors.AGraphvizFormatter;

import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 16:24
 */
public final class GlobalGraphvizParameter extends AGraphvizParameter {

    private final String scope;
    private final List<StringGraphvizParameter> parameters;

    public GlobalGraphvizParameter(String scope, List<StringGraphvizParameter> parameters) {
        this.scope = scope;
        this.parameters = parameters;
    }

    public String getScope() {
        return scope;
    }

    public List<StringGraphvizParameter> getParameters() {
        return parameters;
    }

    @Override
    public String accept(AGraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
