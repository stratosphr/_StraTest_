package graphs.eventb;

import eventb.Event;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:10
 */
public final class ConcreteTransition extends ATransition<ConcreteState> {

    public ConcreteTransition(ConcreteState source, Event event, ConcreteState target) {
        super(source, event, target);
    }

}
