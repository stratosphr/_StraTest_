package eventb.graphs;

import eventb.Event;

/**
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public final class ConcreteTransition extends ATransition<AbstractState> {

    public ConcreteTransition(AbstractState source, Event event, AbstractState target) {
        super(source, event, target);
    }

}
