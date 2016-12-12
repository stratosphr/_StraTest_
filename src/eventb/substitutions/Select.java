package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
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

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public ABoolExpr getCondition() {
        return condition;
    }

    public ASubstitution getSubstitution() {
        return substitution;
    }

    @Override
    public ABoolExpr getPrd(Machine machine) {
        return new And(getCondition(), getSubstitution().getPrd(machine));
    }

}
