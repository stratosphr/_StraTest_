package eventb.graphs;

import eventb.Event;

/**
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public final class ConcreteTransition extends ATransition<ConcreteState> {

    public ConcreteTransition(ConcreteState source, Event event, ConcreteState target) {
        super(source, event, target);
    }

    @Override
    public ConcreteTransition clone() {
        return new ConcreteTransition(getSource().clone(), getEvent().clone(), getTarget().clone());
    }

}
