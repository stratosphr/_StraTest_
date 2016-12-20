package eventb.graphs;

import eventb.Event;

/**
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public abstract class ATransition<State extends AState> {

    private final State source;
    private final Event event;
    private final State target;

    public ATransition(State source, Event event, State target) {
        this.source = source;
        this.event = event;
        this.target = target;
    }

    public State getSource() {
        return source;
    }

    public Event getEvent() {
        return event;
    }

    public State getTarget() {
        return target;
    }

}
