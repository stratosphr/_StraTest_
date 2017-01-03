package algorithms.outputs;

import algorithms.heuristics.EConcreteStateColor;
import eventb.graphs.AbstractState;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.graphs.GraphvizNode;
import utilities.graphviz.graphs.directed.DirectedGraphvizGraph;
import utilities.graphviz.graphs.directed.DirectedGraphvizTransition;
import utilities.graphviz.graphs.parameters.GlobalGraphvizParameter;
import utilities.graphviz.graphs.parameters.StringGraphvizParameter;
import utilities.graphviz.visitors.IGraphvizGraphable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:45
 */
public final class ConcreteTransitionSystem implements IGraphvizGraphable {

    private final LinkedHashSet<ConcreteState> C0;
    private final LinkedHashSet<ConcreteState> C;
    private final LinkedHashMap<ConcreteState, AbstractState> Alpha;
    private final LinkedHashMap<ConcreteState, EConcreteStateColor> Kappa;
    private final LinkedHashSet<ConcreteTransition> DeltaC;

    public ConcreteTransitionSystem(LinkedHashSet<ConcreteState> C0, LinkedHashSet<ConcreteState> C, LinkedHashMap<ConcreteState, AbstractState> Alpha, LinkedHashMap<ConcreteState, EConcreteStateColor> Kappa, LinkedHashSet<ConcreteTransition> DeltaC) {
        this.C0 = C0;
        this.C = C;
        this.Alpha = Alpha;
        this.Kappa = Kappa;
        this.DeltaC = DeltaC;
    }

    @Override
    public DirectedGraphvizGraph getCorrespondingGraphvizGraph() {
        LinkedHashSet<AGraphvizNode> states = C.stream().map(concreteState -> new GraphvizNode(concreteState.getName(), Collections.singletonList(new StringGraphvizParameter("label", concreteState.getExpression().toString())))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> transitions = DeltaC.stream().map(concreteTransition -> new DirectedGraphvizTransition(new GraphvizNode(concreteTransition.getSource().getName()), new GraphvizNode(concreteTransition.getTarget().getName()), Collections.singletonList(new StringGraphvizParameter("label", concreteTransition.getEvent().getName())))).collect(Collectors.toCollection(LinkedHashSet::new));
        return new DirectedGraphvizGraph(states, transitions, Arrays.asList(new StringGraphvizParameter("rankdir", "LR"), new GlobalGraphvizParameter("node", Arrays.asList(new StringGraphvizParameter("shape", "box"), new StringGraphvizParameter("style", Arrays.asList("rounded", "filled")), new StringGraphvizParameter("color", "lightblue2")))));
    }

    public LinkedHashSet<ConcreteState> getC0() {
        return C0;
    }

    public LinkedHashSet<ConcreteState> getC() {
        return C;
    }

    public LinkedHashMap<ConcreteState, AbstractState> getAlpha() {
        return Alpha;
    }

    public LinkedHashMap<ConcreteState, EConcreteStateColor> getKappa() {
        return Kappa;
    }

    public LinkedHashSet<ConcreteTransition> getDeltaC() {
        return DeltaC;
    }

}
