package eventb.substitutions;

import eventb.exprs.arith.IntVariable;
import eventb.exprs.bool.ABoolExpr;
import eventb.visitors.EventBFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:34
 */
public final class Any extends ASubstitution {

    private final ASubstitution substitution;
    private final ABoolExpr condition;
    private final List<IntVariable> quantifiedVariables;

    public Any(ABoolExpr condition, ASubstitution substitution, IntVariable... quantifiedVariables) {
        if (quantifiedVariables.length == 0) {
            throw new Error("A \"ANY\" substitution requires at least one quantified variable declaration (none given).");
        }
        this.quantifiedVariables = Arrays.asList(quantifiedVariables);
        this.condition = condition;
        this.substitution = substitution;
    }

    public List<IntVariable> getQuantifiedVariables() {
        return quantifiedVariables;
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
