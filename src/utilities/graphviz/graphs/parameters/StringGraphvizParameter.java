package utilities.graphviz.graphs.parameters;

import utilities.graphviz.visitors.AGraphvizFormatter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 16:24
 */
public final class StringGraphvizParameter extends ANonGlobalGraphvizParameter {

    private final String name;
    private final String value;

    public StringGraphvizParameter(String name, List<String> values) {
        this(name, values.stream().collect(Collectors.joining(", ")));
    }

    public StringGraphvizParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String accept(AGraphvizFormatter visitor) {
        return visitor.visit(this);
    }

}
