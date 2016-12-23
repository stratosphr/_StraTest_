package utilities.graphviz.visitors;

import utilities.graphviz.graphs.AGraphvizGraph;
import utilities.graphviz.graphs.AGraphvizTransition;

/**
 * Created by gvoiron on 23/12/16.
 * Time : 02:30
 */
public interface IGraphvizGraphable<Transition extends AGraphvizTransition> {

    AGraphvizGraph<Transition> getCorrespondingGraphvizGraph();

}
