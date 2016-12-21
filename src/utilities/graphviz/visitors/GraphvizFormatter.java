package utilities.graphviz.visitors;

import utilities.AFormatter;
import utilities.graphviz.graphs.AGraphvizCluster;
import utilities.graphviz.graphs.GraphvizNode;
import utilities.graphviz.graphs.directed.DirectedGraphvizGraph;
import utilities.graphviz.graphs.directed.DirectedGraphvizTransition;
import utilities.graphviz.graphs.parameters.GlobalGraphvizParameter;
import utilities.graphviz.graphs.parameters.GraphvizParameter;
import utilities.graphviz.graphs.undirected.UndirectedGraphvizGraph;
import utilities.graphviz.graphs.undirected.UndirectedGraphvizTransition;

import java.util.stream.Collectors;

import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 15:02
 */
public final class GraphvizFormatter extends AFormatter {

    public String visit(UndirectedGraphvizGraph undirectedGraphvizGraph) {
        String formatted = "";
        formatted += "graph {" + NEW_LINE;
        formatted += NEW_LINE;
        indentRight();
        formatted += indent() + undirectedGraphvizGraph.getNodes().stream().map(state -> state.accept(this)).collect(Collectors.joining(NEW_LINE + indent())) + NEW_LINE;
        if (!undirectedGraphvizGraph.getTransitions().isEmpty()) {
            formatted += NEW_LINE;
            formatted += indent() + undirectedGraphvizGraph.getTransitions().stream().map(transition -> transition.accept(this)).collect(Collectors.joining(NEW_LINE + indent())) + NEW_LINE;
        }
        indentLeft();
        formatted += NEW_LINE;
        formatted += indent() + "}" + NEW_LINE;
        return formatted;
    }

    public String visit(DirectedGraphvizGraph directedGraphvizGraph) {
        String formatted = "";
        formatted += "digraph {" + NEW_LINE;
        formatted += NEW_LINE;
        indentRight();
        formatted += indent() + directedGraphvizGraph.getParameters().stream().map(parameter -> parameter.accept(this) + ";").collect(Collectors.joining(NEW_LINE + indent())) + NEW_LINE;
        formatted += NEW_LINE;
        formatted += indent() + directedGraphvizGraph.getNodes().stream().map(state -> state.accept(this)).collect(Collectors.joining(NEW_LINE + indent())) + NEW_LINE;
        if (!directedGraphvizGraph.getTransitions().isEmpty()) {
            formatted += NEW_LINE;
            formatted += indent() + directedGraphvizGraph.getTransitions().stream().map(transition -> transition.accept(this)).collect(Collectors.joining(NEW_LINE + indent())) + NEW_LINE;
        }
        indentLeft();
        formatted += NEW_LINE;
        formatted += indent() + "}" + NEW_LINE;
        return formatted;
    }

    public String visit(GraphvizNode node) {
        return node.getName() + node.getParameters().stream().map(parameter -> parameter.accept(this)).collect(Collectors.joining(", ", "[", "]")) + ";";
    }

    public String visit(AGraphvizCluster cluster) {
        String formatted = "";
        formatted += "cluster_" + cluster.getName() + " {" + NEW_LINE;
        formatted += indent() + "}";
        return formatted;
    }

    public String visit(UndirectedGraphvizTransition undirectedGraphvizTransition) {
        return undirectedGraphvizTransition.getSource().getName() + " - " + undirectedGraphvizTransition.getTarget().getName() + ";";
    }

    public String visit(DirectedGraphvizTransition directedGraphvizTransition) {
        return directedGraphvizTransition.getSource().getName() + " -> " + directedGraphvizTransition.getTarget().getName() + ";";
    }

    public String visit(GraphvizParameter graphvizParameter) {
        return graphvizParameter.getName() + "=\"" + graphvizParameter.getValue() + "\"";
    }

    public String visit(GlobalGraphvizParameter globalGraphvizParameter) {
        return globalGraphvizParameter.getScope() + globalGraphvizParameter.getParameters().stream().map(parameter -> parameter.accept(this)).collect(Collectors.joining(", ", "[", "]"));
    }

}
