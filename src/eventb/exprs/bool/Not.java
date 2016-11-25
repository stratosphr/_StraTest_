package eventb.exprs.bool;

import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:56
 */
public final class Not extends ABoolExpr {

    private final ABoolExpr operand;

    public Not(ABoolExpr operand) {
        this.operand = operand;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public ABoolExpr getOperand() {
        return operand;
    }

}
