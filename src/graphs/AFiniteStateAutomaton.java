package graphs;

import java.util.*;
import java.util.stream.Collectors;

import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 09/12/16.
 * Time : 14:49
 */
public abstract class AFiniteStateAutomaton<State, Label, Transition extends ALabelledTransition<State, Label>> {

    private final Set<State> states;
    private final Set<State> initialStates;
    private final Set<State> finalStates;
    private final Set<Transition> transitions;
    private final Map<State, Map<Label, Set<State>>> transitionsTable;

    public AFiniteStateAutomaton(Set<State> states, Set<State> initialStates, Set<State> finalStates, Set<Transition> transitions) {
        this.states = new HashSet<>();
        this.initialStates = new HashSet<>(initialStates);
        this.finalStates = new HashSet<>(finalStates);
        this.transitions = new HashSet<>();
        this.transitionsTable = new HashMap<>();
        states.forEach(this::addState);
        initialStates.forEach(this::addState);
        finalStates.forEach(this::addState);
        transitions.forEach(this::addTransition);
    }

    public void addState(State state) {
        if (states.add(state)) {
            getTransitionsTable().put(state, new HashMap<>());
        }
    }

    private void addTransition(Transition transition) {
        addState(transition.getSource());
        addState(transition.getTarget());
        getTransitions().add(transition);
        if (!getTransitionsTable().get(transition.getSource()).containsKey(transition.getLabel())) {
            getTransitionsTable().get(transition.getSource()).put(transition.getLabel(), new HashSet<>());
        }
        getTransitionsTable().get(transition.getSource()).get(transition.getLabel()).add(transition.getTarget());
    }

    public Set<Transition> findReachableTransitions(State root) {
        return findReachableTransitions_(root, new LinkedHashSet<>());
    }

    public Set<Transition> findReachableTransitions_(State root, Set<Transition> visitedTransitions) {
        for (Transition transition : getTransitions().stream().filter(transition -> transition.getSource().equals(root)).collect(Collectors.toList())) {
            if (visitedTransitions.add(transition)) {
                findReachableTransitions_(transition.getTarget(), visitedTransitions);
            }
        }
        return visitedTransitions;
    }

    public Set<State> getStates() {
        return states;
    }

    public Set<State> getInitialStates() {
        return initialStates;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public Map<State, Map<Label, Set<State>>> getTransitionsTable() {
        return transitionsTable;
    }

    @Override
    public String toString() {
        String str = "";
        str += states.stream().filter(state -> getTransitions().stream().noneMatch(transition -> transition.getSource().equals(state) || transition.getTarget().equals(state))).map(Object::toString).collect(Collectors.joining(NEW_LINE)) + NEW_LINE;
        for (State state : getTransitionsTable().keySet()) {
            for (Label label : getTransitionsTable().get(state).keySet()) {
                if (getInitialStates().contains(state)) {
                    str += "-> ";
                }
                str += (getFinalStates().contains(state) ? "(" + state + ")" : state) + " -[ " + label + " ]-> " + getTransitionsTable().get(state).get(label).stream().map(target -> getFinalStates().contains(target) ? "(" + target + ")" : target.toString()).collect(Collectors.joining(", ", "[", "]")) + NEW_LINE;
            }
        }
        return str;
    }

}
