package algorithms.outputs;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:38
 */
public final class ApproximatedTransitionSystem {

    private final EventSystem eventSystem;
    private final TriModalTransitionSystem triModalTransitionSystem;
    private final ConcreteTransitionSystem concreteTransitionSystem;

    public ApproximatedTransitionSystem(EventSystem eventSystem, TriModalTransitionSystem triModalTransitionSystem, ConcreteTransitionSystem concreteTransitionSystem) {
        this.eventSystem = eventSystem;
        this.triModalTransitionSystem = triModalTransitionSystem;
        this.concreteTransitionSystem = concreteTransitionSystem;
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }

    public TriModalTransitionSystem getTriModalTransitionSystem() {
        return triModalTransitionSystem;
    }

    public ConcreteTransitionSystem getConcreteTransitionSystem() {
        return concreteTransitionSystem;
    }

}
