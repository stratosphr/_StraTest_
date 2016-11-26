package eventb;

import eventb.visitors.EventBFormatter;
import eventb.visitors.IEventBFormatterVisitable;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:31
 */
public abstract class AEventBObject implements IEventBFormatterVisitable {

    @Override
    public final String toString() {
        return accept(new EventBFormatter());
    }

}
