package utilities.graphviz.graphs;

import utilities.graphviz.AGraphvizObject;
import utilities.graphviz.graphs.parameters.GraphvizParameter;

import java.util.Collections;
import java.util.List;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 14:57
 */
public abstract class AGraphvizNode extends AGraphvizObject {

    private final List<GraphvizParameter> parameters;
    private String name;

    public AGraphvizNode(String name) {
        this(name, Collections.emptyList());
    }

    public AGraphvizNode(String name, List<GraphvizParameter> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public List<GraphvizParameter> getParameters() {
        return parameters;
    }

}
