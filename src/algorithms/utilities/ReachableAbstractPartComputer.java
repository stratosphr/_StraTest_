package algorithms.utilities;

import algorithms.AComputer;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import utilities.sets.Tuple;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 09/01/17.
 * Time : 13:28
 */
public final class ReachableAbstractPartComputer extends AComputer<Tuple<LinkedHashSet<AbstractState>, LinkedHashSet<AbstractTransition>>> {

    private final LinkedHashSet<ConcreteState> initialStates;
    private final LinkedHashSet<ConcreteTransition> transitions;
    private LinkedHashMap<ConcreteState, AbstractState> alpha;
    private final LinkedHashSet<AbstractState> reachableStates;
    private final LinkedHashSet<AbstractTransition> reachableTransitions;

    public ReachableAbstractPartComputer(LinkedHashSet<ConcreteState> initialStates, LinkedHashSet<ConcreteTransition> transitions, LinkedHashMap<ConcreteState, AbstractState> alpha) {
        this.initialStates = initialStates;
        this.transitions = transitions;
        this.alpha = alpha;
        reachableStates = new LinkedHashSet<>();
        reachableTransitions = new LinkedHashSet<>();
    }

    @Override
    protected Tuple<LinkedHashSet<AbstractState>, LinkedHashSet<AbstractTransition>> compute_() {
        Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> result = new ReachableConcretePartComputer(initialStates, transitions).compute().getResult();
        return new Tuple<>(result.getFirst().stream().map(concreteState -> alpha.get(concreteState)).collect(Collectors.toCollection(LinkedHashSet::new)), result.getSecond().stream().map(concreteTransition -> new AbstractTransition(alpha.get(concreteTransition.getSource()), concreteTransition.getEvent(), alpha.get(concreteTransition.getTarget()))).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

}
