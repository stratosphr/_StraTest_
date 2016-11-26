package eventb.events;

import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:30
 */
public final class Skip extends ASubstitution {

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

}
