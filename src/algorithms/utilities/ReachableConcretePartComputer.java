package algorithms.utilities;

import algorithms.AComputer;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import utilities.sets.Tuple;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 09/01/17.
 * Time : 13:28
 */
public final class ReachableConcretePartComputer extends AComputer<Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>>> {

    private final LinkedHashSet<ConcreteState> initialStates;
    private final LinkedHashSet<ConcreteTransition> transitions;
    private final LinkedHashSet<ConcreteState> reachableStates;
    private final LinkedHashSet<ConcreteTransition> reachableTransitions;

    public ReachableConcretePartComputer(LinkedHashSet<ConcreteState> initialStates, LinkedHashSet<ConcreteTransition> transitions) {
        this.initialStates = initialStates;
        this.transitions = transitions;
        this.reachableStates = new LinkedHashSet<>(initialStates);
        this.reachableTransitions = new LinkedHashSet<>();
    }

    @Override
    protected Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> compute_() {
        while (transitions.stream().anyMatch(transition -> !reachableTransitions.contains(transition) && reachableStates.contains(transition.getSource()))) {
            transitions.stream().filter(transition -> !reachableTransitions.contains(transition) && reachableStates.contains(transition.getSource())).forEach(transition -> {
                reachableStates.addAll(Arrays.asList(transition.getSource(), transition.getTarget()));
                reachableTransitions.add(transition);
            });
        }
        return new Tuple<>(reachableStates, reachableTransitions);
    }

}
