package graphs.eventb;

import eventb.Event;
import graphs.ALabelledTransition;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:10
 */
public abstract class ATransition<State> extends ALabelledTransition<State, Event> {

    public ATransition(State source, Event event, State target) {
        super(source, event, target);
    }

    public final Event getEvent() {
        return super.getLabel();
    }

    @Override
    public String toString() {
        return getSource().toString() + " -[ " + getEvent().getName() + " ]-> " + getTarget().toString();
    }

}
