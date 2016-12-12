package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.Not;
import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:34
 */
public final class IfThenElse extends ASubstitution {

    private final ABoolExpr condition;
    private final ASubstitution thenPart;
    private final ASubstitution elsePart;

    public IfThenElse(ABoolExpr condition, ASubstitution thenPart) {
        this(condition, thenPart, new Skip());
    }

    public IfThenElse(ABoolExpr condition, ASubstitution thenPart, ASubstitution elsePart) {
        this.condition = condition;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }

    @Override
    public ABoolExpr getPrd(Machine machine) {
        return new Choice(new Select(getCondition(), getThenPart()), new Select(new Not(getCondition()), getElsePart())).getPrd(machine);
    }

    public ABoolExpr getCondition() {
        return condition;
    }

    public ASubstitution getThenPart() {
        return thenPart;
    }

    public ASubstitution getElsePart() {
        return elsePart;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

}
