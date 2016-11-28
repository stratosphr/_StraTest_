package eventb.exprs.bool;

import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 27/11/16.
 * Time : 12:54
 */
public final class Invariant extends ABoolExpr {

    private ABoolExpr expression;

    public Invariant(ABoolExpr expression) {
        this.expression = expression;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public ABoolExpr getExpression() {
        return expression;
    }

}
