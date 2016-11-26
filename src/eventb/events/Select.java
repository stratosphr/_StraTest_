package eventb.events;

import eventb.exprs.bool.ABoolExpr;
import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:34
 */
public final class Select extends ASubstitution {

    private final ASubstitution substitution;
    private final ABoolExpr condition;

    public Select(ABoolExpr condition, ASubstitution substitution) {
        this.condition = condition;
        this.substitution = substitution;
    }

    public ABoolExpr getCondition() {
        return condition;
    }

    public ASubstitution getSubstitution() {
        return substitution;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

}
