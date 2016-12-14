package algorithms.visitors;

import algorithms.utilities.ApproximatedTransitionSystem;
import algorithms.utilities.EventSystemUnderApproximation;
import algorithms.utilities.TriModalTransitionSystem;
import graphs.dot.*;
import graphs.eventb.AbstractState;
import utilities.AFormatter;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:05
 */
public final class DOTFormatter extends AFormatter {

    private final boolean fullLabels;

    public DOTFormatter() {
        this(true);
    }

    public DOTFormatter(boolean fullLabels) {
        this.fullLabels = fullLabels;
    }

    public String visit(TriModalTransitionSystem triModalTransitionSystem) {
        return triModalTransitionSystem.toString();
    }

    public String visit(EventSystemUnderApproximation eventSystemUnderApproximation) {
        return eventSystemUnderApproximation.toString();
    }

    public String visit(ApproximatedTransitionSystem approximatedTransitionSystem) {
        LinkedHashSet<ADOTState> states = new LinkedHashSet<>();
        for (AbstractState abstractState : approximatedTransitionSystem.getTriModalTransitionSystem().getQ()) {
            LinkedHashSet<ADOTState> substates = approximatedTransitionSystem.getEventSystemUnderApproximation().getC().stream().filter(concreteState -> approximatedTransitionSystem.getEventSystemUnderApproximation().getAlpha().get(concreteState).equals(abstractState)).map(concreteState -> new DOTState(concreteState.getName(), concreteState.toString())).collect(Collectors.toCollection(LinkedHashSet::new));
            LinkedHashSet<ADOTTransition> subtransitions = new LinkedHashSet<>();
            states.add(new DOTCluster(abstractState.getName(), abstractState.toString(), substates, subtransitions));
        }
        LinkedHashSet<DirectedDOTTransition> transitions = approximatedTransitionSystem.getEventSystemUnderApproximation().getDeltaC().stream().map(concreteTransition -> new DirectedDOTTransition(new DOTState(concreteTransition.getSource().getName(), concreteTransition.getSource().toString()), concreteTransition.getLabel().getName(), new DOTState(concreteTransition.getTarget().getName(), concreteTransition.getTarget().toString()))).collect(Collectors.toCollection(LinkedHashSet::new));
        DirectedDOTGraph directedDOTGraph = new DirectedDOTGraph(states, transitions);
        return directedDOTGraph.accept(this);
    }

    public String visit(DirectedDOTGraph directedDOTGraph) {
        String formatted = "";
        formatted += "digraph {" + NEW_LINE;
        formatted += NEW_LINE;
        indentRight();
        formatted += indent() + "rankdir = LR;" + NEW_LINE;
        formatted += indent() + "node[shape=\"box\", style=\"rounded,filled\", color=\"lightblue2\"];" + NEW_LINE;
        formatted += NEW_LINE;
        formatted += indent() + directedDOTGraph.getStates().stream().map(state -> state.accept(this)).collect(Collectors.joining(NEW_LINE + NEW_LINE + indent())) + NEW_LINE;
        formatted += NEW_LINE;
        formatted += indent() + directedDOTGraph.getTransitions().stream().map(transition -> transition.accept(this)).collect(Collectors.joining(NEW_LINE + indent())) + NEW_LINE;
        indentLeft();
        formatted += NEW_LINE;
        formatted += "}";
        return formatted;
    }

    public String visit(DOTCluster dotCluster) {
        String formatted = "";
        formatted += "subgraph cluster_" + dotCluster.getName() + " {" + NEW_LINE;
        indentRight();
        formatted += indent() + "label=<<B>" + (fullLabels ? dotCluster.getLabel() : dotCluster.getName()) + "</B>>;" + NEW_LINE;
        formatted += indent() + dotCluster.getStates().stream().map(state -> state.accept(this)).collect(Collectors.joining(NEW_LINE + indent())) + NEW_LINE;
        indentLeft();
        formatted += indent() + "}";
        return formatted;
    }

    public String visit(DOTState dotState) {
        return dotState.getName() + "[label=\"" + (fullLabels ? dotState.getLabel() : dotState.getName()) + "\"];";
    }

    public String visit(DirectedDOTTransition directedDOTTransition) {
        return directedDOTTransition.getSource().getName() + " -> " + directedDOTTransition.getTarget().getName() + " [label=\"" + directedDOTTransition.getLabel() + "\"]";
    }

}
