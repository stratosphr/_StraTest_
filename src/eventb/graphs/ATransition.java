package eventb.graphs;

import eventb.Event;
import utilities.ICloneable;

/**
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public abstract class ATransition<State extends AState> implements ICloneable<ATransition<State>> {

    private final State source;
    private final Event event;
    private final State target;

    public ATransition(State source, Event event, State target) {
        this.source = source;
        this.event = event;
        this.target = target;
    }

    @Override
    public abstract ATransition<State> clone();

    public State getSource() {
        return source;
    }

    public Event getEvent() {
        return event;
    }

    public State getTarget() {
        return target;
    }

    @Override
    public final int hashCode() {
        return (getClass() + "_" + toString()).hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return getClass().equals(o.getClass()) && ((this == o) || toString().equals(o.toString()));
    }

    @Override
    public final String toString() {
        return getSource() + " -[ " + getEvent().getName() + " ]-> " + getTarget();
    }

}
