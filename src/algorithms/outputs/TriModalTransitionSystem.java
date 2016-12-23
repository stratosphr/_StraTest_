package algorithms.outputs;

import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import utilities.graphviz.graphs.AGraphvizGraph;
import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.graphs.GraphvizNode;
import utilities.graphviz.graphs.directed.DirectedGraphvizGraph;
import utilities.graphviz.graphs.directed.DirectedGraphvizTransition;
import utilities.graphviz.graphs.parameters.GlobalGraphvizParameter;
import utilities.graphviz.graphs.parameters.GraphvizParameter;
import utilities.graphviz.visitors.IGraphvizGraphable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:42
 */
public final class TriModalTransitionSystem implements IGraphvizGraphable<DirectedGraphvizTransition> {

    private final LinkedHashSet<AbstractState> Q0;
    private final LinkedHashSet<AbstractState> Q;
    private final LinkedHashSet<AbstractTransition> Delta;
    private final LinkedHashSet<AbstractTransition> DeltaMinus;
    private final LinkedHashSet<AbstractTransition> DeltaPlus;

    public TriModalTransitionSystem(LinkedHashSet<AbstractState> Q0, LinkedHashSet<AbstractState> Q, LinkedHashSet<AbstractTransition> Delta, LinkedHashSet<AbstractTransition> DeltaMinus, LinkedHashSet<AbstractTransition> DeltaPlus) {
        this.Q0 = Q0;
        this.Q = Q;
        this.Delta = Delta;
        this.DeltaMinus = DeltaMinus;
        this.DeltaPlus = DeltaPlus;
    }

    @Override
    public AGraphvizGraph<DirectedGraphvizTransition> getCorrespondingGraphvizGraph() {
        LinkedHashSet<AGraphvizNode> states = getQ().stream().map(abstractState -> new GraphvizNode(abstractState.getName(), Collections.singletonList(new GraphvizParameter("label", abstractState.getExpression().toString())))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> transitions = new LinkedHashSet<>();
        LinkedHashSet<DirectedGraphvizTransition> DeltaSharp = DeltaMinus.stream().filter(DeltaPlus::contains).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getSource().getExpression().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getTarget().getExpression().toString()))), Collections.singletonList(new GraphvizParameter("label", "# " + abstractTransition.getEvent().getName())))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> DeltaPureMinus = DeltaMinus.stream().filter(abstractTransition -> !DeltaPlus.contains(abstractTransition)).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getSource().getExpression().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getTarget().getExpression().toString()))), Collections.singletonList(new GraphvizParameter("label", "- " + abstractTransition.getEvent().getName())))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> DeltaPurePlus = DeltaPlus.stream().filter(abstractTransition -> !DeltaMinus.contains(abstractTransition)).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getSource().getExpression().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getTarget().getExpression().toString()))), Collections.singletonList(new GraphvizParameter("label", "+ " + abstractTransition.getEvent().getName())))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> DeltaPureMay = Delta.stream().filter(abstractTransition -> !DeltaMinus.contains(abstractTransition) && !DeltaPlus.contains(abstractTransition)).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getSource().getExpression().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getTarget().getExpression().toString()))), Collections.singletonList(new GraphvizParameter("label", abstractTransition.getEvent().getName())))).collect(Collectors.toCollection(LinkedHashSet::new));
        transitions.addAll(Stream.of(DeltaSharp, DeltaPureMinus, DeltaPurePlus, DeltaPureMay).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new)));
        return new DirectedGraphvizGraph(states, transitions, Arrays.asList(new GraphvizParameter("rankdir", "LR"), new GlobalGraphvizParameter("node", Arrays.asList(new GraphvizParameter("shape", "box"), new GraphvizParameter("style", "rounded,filled"), new GraphvizParameter("color", "lightblue2")))));
    }

    public LinkedHashSet<AbstractState> getQ0() {
        return Q0;
    }

    public LinkedHashSet<AbstractState> getQ() {
        return Q;
    }

    public LinkedHashSet<AbstractTransition> getDelta() {
        return Delta;
    }

    public LinkedHashSet<AbstractTransition> getDeltaMinus() {
        return DeltaMinus;
    }

    public LinkedHashSet<AbstractTransition> getDeltaPlus() {
        return DeltaPlus;
    }

}
