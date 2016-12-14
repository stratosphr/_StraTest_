package eventb.exprs.bool;

import eventb.exprs.arith.IntVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.LinkedHashSet;

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
    public ABoolExpr accept(Primer visitor) {
        return visitor.visit(this);
    }

    @Override
    public ABoolExpr accept(UnPrimer visitor) {
        return visitor.visit(this);
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public String accept(SMTLibFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public LinkedHashSet<IntVariable> getVariables() {
        return getExpression().getVariables();
    }

    public ABoolExpr getExpression() {
        return expression;
    }

}
