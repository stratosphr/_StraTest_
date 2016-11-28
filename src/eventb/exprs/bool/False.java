package eventb.exprs.bool;

import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 28/11/16.
 * Time : 08:59
 */
public final class False extends ABoolExpr {

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

}
