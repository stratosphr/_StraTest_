package algorithms.outputs;

import utilities.ICloneable;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:38
 */
public final class ApproximatedTransitionSystem implements ICloneable<ApproximatedTransitionSystem> {

    private final EventSystem eventSystem;
    private final TriModalTransitionSystem triModalTransitionSystem;
    private final ConcreteTransitionSystem concreteTransitionSystem;

    public ApproximatedTransitionSystem(EventSystem eventSystem, TriModalTransitionSystem triModalTransitionSystem, ConcreteTransitionSystem concreteTransitionSystem) {
        this.eventSystem = eventSystem;
        this.triModalTransitionSystem = triModalTransitionSystem;
        this.concreteTransitionSystem = concreteTransitionSystem;
    }

    @Override
    public ApproximatedTransitionSystem clone() {
        return new ApproximatedTransitionSystem(getEventSystem().clone(), get3MTS().clone(), getCTS().clone());
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }

    public TriModalTransitionSystem get3MTS() {
        return triModalTransitionSystem;
    }

    public ConcreteTransitionSystem getCTS() {
        return concreteTransitionSystem;
    }

}
