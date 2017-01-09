package algorithms.outputs;

import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import utilities.ICloneable;
import utilities.graphviz.graphs.AGraphvizNode;
import utilities.graphviz.graphs.GraphvizNode;
import utilities.graphviz.graphs.directed.DirectedGraphvizGraph;
import utilities.graphviz.graphs.directed.DirectedGraphvizTransition;
import utilities.graphviz.graphs.parameters.GlobalGraphvizParameter;
import utilities.graphviz.graphs.parameters.HtmlGraphvizParameter;
import utilities.graphviz.graphs.parameters.StringGraphvizParameter;
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
public final class TriModalTransitionSystem implements IGraphvizGraphable, ICloneable<TriModalTransitionSystem> {

    private final LinkedHashSet<AbstractState> Q0;
    private final LinkedHashSet<AbstractState> Q;
    private final LinkedHashSet<AbstractTransition> Delta;
    private final LinkedHashSet<AbstractTransition> DeltaMinus;
    private final LinkedHashSet<AbstractTransition> DeltaPlus;
    private final LinkedHashSet<AbstractTransition> DeltaPureMay;
    private final LinkedHashSet<AbstractTransition> DeltaPureMinus;
    private final LinkedHashSet<AbstractTransition> DeltaPurePlus;
    private final LinkedHashSet<AbstractTransition> DeltaSharp;

    public TriModalTransitionSystem(LinkedHashSet<AbstractState> Q0, LinkedHashSet<AbstractState> Q, LinkedHashSet<AbstractTransition> Delta, LinkedHashSet<AbstractTransition> DeltaMinus, LinkedHashSet<AbstractTransition> DeltaPlus) {
        this.Q0 = Q0;
        this.Q = Q;
        this.Delta = Delta;
        this.DeltaMinus = DeltaMinus;
        this.DeltaPlus = DeltaPlus;
        this.DeltaPureMay = Delta.stream().filter(abstractTransition -> !DeltaMinus.contains(abstractTransition) && !DeltaPlus.contains(abstractTransition)).collect(Collectors.toCollection(LinkedHashSet::new));
        this.DeltaPureMinus = DeltaMinus.stream().filter(abstractTransition -> !DeltaPlus.contains(abstractTransition)).collect(Collectors.toCollection(LinkedHashSet::new));
        this.DeltaPurePlus = DeltaPlus.stream().filter(abstractTransition -> !DeltaMinus.contains(abstractTransition)).collect(Collectors.toCollection(LinkedHashSet::new));
        this.DeltaSharp = DeltaMinus.stream().filter(DeltaPlus::contains).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public TriModalTransitionSystem clone() {
        return new TriModalTransitionSystem(new LinkedHashSet<>(getQ0()), new LinkedHashSet<>(getQ()), new LinkedHashSet<>(getDelta()), new LinkedHashSet<>(getDeltaMinus()), new LinkedHashSet<>(getDeltaPlus()));
    }

    @Override
    public DirectedGraphvizGraph getCorrespondingGraphvizGraph() {
        LinkedHashSet<AGraphvizNode> states = getQ().stream().map(abstractState -> new GraphvizNode(abstractState.getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractState.toString())))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> transitions = new LinkedHashSet<>();
        LinkedHashSet<DirectedGraphvizTransition> DeltaPureMay = Delta.stream().filter(abstractTransition -> !DeltaMinus.contains(abstractTransition) && !DeltaPlus.contains(abstractTransition)).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getSource().getExpression().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getTarget().toString()))), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getEvent().getName())))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> DeltaSharp = DeltaMinus.stream().filter(DeltaPlus::contains).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getSource().getExpression().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getTarget().toString()))), Collections.singletonList(new HtmlGraphvizParameter("label", "<" + abstractTransition.getEvent().getName() + "<sup>#</sup>>")))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> DeltaPureMinus = DeltaMinus.stream().filter(abstractTransition -> !DeltaPlus.contains(abstractTransition)).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getSource().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getTarget().toString()))), Collections.singletonList(new HtmlGraphvizParameter("label", "<" + abstractTransition.getEvent().getName() + "<sup>-</sup>>")))).collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashSet<DirectedGraphvizTransition> DeltaPurePlus = DeltaPlus.stream().filter(abstractTransition -> !DeltaMinus.contains(abstractTransition)).map(abstractTransition -> new DirectedGraphvizTransition(new GraphvizNode(abstractTransition.getSource().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getSource().toString()))), new GraphvizNode(abstractTransition.getTarget().getName(), Collections.singletonList(new StringGraphvizParameter("label", abstractTransition.getTarget().toString()))), Collections.singletonList(new HtmlGraphvizParameter("label", "<" + abstractTransition.getEvent().getName() + "<sup>+</sup>>")))).collect(Collectors.toCollection(LinkedHashSet::new));
        transitions.addAll(Stream.of(DeltaPureMay, DeltaSharp, DeltaPureMinus, DeltaPurePlus).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new)));
        return new DirectedGraphvizGraph(states, transitions, Arrays.asList(new StringGraphvizParameter("rankdir", "LR"), new GlobalGraphvizParameter("node", Arrays.asList(new StringGraphvizParameter("shape", "box"), new StringGraphvizParameter("style", "rounded,filled"), new StringGraphvizParameter("color", "lightblue2")))));
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

    public LinkedHashSet<AbstractTransition> getDeltaPureMay() {
        return DeltaPureMay;
    }

    public LinkedHashSet<AbstractTransition> getDeltaPureMinus() {
        return DeltaPureMinus;
    }

    public LinkedHashSet<AbstractTransition> getDeltaPurePlus() {
        return DeltaPurePlus;
    }

    public LinkedHashSet<AbstractTransition> getDeltaSharp() {
        return DeltaSharp;
    }

}
