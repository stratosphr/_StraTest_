package graphs.eventb;

import eventb.Event;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:10
 */
public final class AbstractTransition extends ATransition<AbstractState> {

    public AbstractTransition(AbstractState source, Event event, AbstractState target) {
        super(source, event, target);
    }

}
