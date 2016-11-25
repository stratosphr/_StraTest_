package eventb.exprs;

import eventb.visitors.EventBFormatter;
import eventb.visitors.IEventBFormatterVisitable;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:16
 */
public abstract class AExpr implements IEventBFormatterVisitable {

    @Override
    public final String toString() {
        return accept(new EventBFormatter());
    }

}
