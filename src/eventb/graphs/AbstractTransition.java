package eventb.graphs;

import eventb.Event;

/**
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public final class AbstractTransition extends ATransition<AbstractState> {

    public AbstractTransition(AbstractState source, Event event, AbstractState target) {
        super(source, event, target);
    }

    @Override
    public AbstractTransition clone() {
        return new AbstractTransition(getSource().clone(), getEvent().clone(), getTarget().clone());
    }

}
