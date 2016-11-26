package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:41
 */
public final class Int extends AArithExpr {

    private final Integer value;

    public Int(Integer value) {
        this.value = value;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public Integer getValue() {
        return value;
    }

}
