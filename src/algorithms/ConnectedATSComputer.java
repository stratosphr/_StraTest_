package algorithms;

import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ComputerResult;
import algorithms.utilities.ReachableConcretePartComputer;
import eventb.Event;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import eventb.substitutions.Skip;
import utilities.sets.Tuple;

import java.util.*;

/**
 * Created by gvoiron on 10/03/17.
 * Time : 15:13
 */
public class ConnectedATSComputer extends AComputer<ApproximatedTransitionSystem> {

    private final ApproximatedTransitionSystem approximatedTransitionSystem;
    private final ApproximatedTransitionSystem connectedApproximatedTransitionSystem;

    public ConnectedATSComputer(ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.approximatedTransitionSystem = approximatedTransitionSystem;
        this.connectedApproximatedTransitionSystem = approximatedTransitionSystem.clone();
    }

    @Override
    protected ApproximatedTransitionSystem compute_() {
        LinkedHashSet<ConcreteState> toBeConnected = new LinkedHashSet<>();
        Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> reachablePart = new ReachableConcretePartComputer(approximatedTransitionSystem.getConcreteTransitionSystem().getC0(), approximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC()).compute().getResult();
        Map<ConcreteState, Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>>> subReachableParts = new LinkedHashMap<>();
        reachablePart.getFirst().forEach(concreteState -> subReachableParts.put(concreteState, new ReachableConcretePartComputer(new LinkedHashSet<>(Collections.singleton(concreteState)), approximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC()).compute().getResult()));
        subReachableParts.keySet().forEach(concreteState -> {
            if (subReachableParts.get(concreteState).getFirst().size() == 1 || subReachableParts.get(concreteState).getSecond().stream().noneMatch(concreteTransition -> concreteTransition.getSource().equals(concreteState) && !concreteTransition.getTarget().equals(concreteState))) {
                toBeConnected.add(concreteState);
            }
        });
        subReachableParts.keySet().forEach(concreteState -> {
            if (subReachableParts.get(concreteState).getFirst().stream().noneMatch(toBeConnected::contains)) {
                toBeConnected.add(concreteState);
            }
        });
        ConcreteState fictive = new ConcreteState("_fictive_", new TreeMap<>());
        Event reset = new Event("_reset_", new Skip());
        Event beta = new Event("_beta_", new Skip());
        toBeConnected.forEach(concreteState -> connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().add(new ConcreteTransition(concreteState, reset, fictive)));
        connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getC0().forEach(concreteState -> connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().add(new ConcreteTransition(fictive, beta, concreteState)));
        connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getC0().clear();
        connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getC0().add(fictive);
        connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getC().clear();
        ComputerResult<Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>>> compute = new ReachableConcretePartComputer(connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getC0(), connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC()).compute();
        connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getC().addAll(compute.getResult().getFirst());
        connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().clear();
        connectedApproximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().addAll(compute.getResult().getSecond());
        return connectedApproximatedTransitionSystem;
    }

}
