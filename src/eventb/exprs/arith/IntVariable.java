package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:19
 */
public final class IntVariable extends AAssignable {

    public IntVariable(String name) {
        super(name);
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

}
