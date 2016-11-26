package eventb.events;

import eventb.exprs.arith.AArithExpr;
import eventb.exprs.arith.AAssignable;
import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:43
 */
public final class Assignment extends ASubstitution {

    private final AAssignable assignable;
    private final AArithExpr value;

    public Assignment(AAssignable assignable, AArithExpr value) {
        this.assignable = assignable;
        this.value = value;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public AAssignable getAssignable() {
        return assignable;
    }

    public AArithExpr getValue() {
        return value;
    }

}
