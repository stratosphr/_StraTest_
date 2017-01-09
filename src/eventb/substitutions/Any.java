package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.arith.QuantifiedVariable;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Exists;
import eventb.visitors.EventBFormatter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:34
 */
public final class Any extends ASubstitution {

    private final ASubstitution substitution;
    private final ABoolExpr condition;
    private final Set<QuantifiedVariable> quantifiedVariables;

    public Any(ABoolExpr condition, ASubstitution substitution, QuantifiedVariable... quantifiedVariables) {
        if (quantifiedVariables.length == 0) {
            throw new Error("A \"ANY\" substitution requires at least one quantified variable declaration (none given).");
        }
        this.quantifiedVariables = new LinkedHashSet<>(Arrays.asList(quantifiedVariables));
        this.condition = condition;
        this.substitution = substitution;
    }

    @Override
    public Any clone() {
        return new Any(getCondition().clone(), getSubstitution().clone(), getQuantifiedVariables().stream().map(QuantifiedVariable::clone).toArray(QuantifiedVariable[]::new));
    }

    @Override
    public ABoolExpr getPrd(Machine machine) {
        return new Exists(new And(getCondition(), getSubstitution().getPrd(machine)), getQuantifiedVariables().stream().toArray(QuantifiedVariable[]::new));
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public Set<QuantifiedVariable> getQuantifiedVariables() {
        return quantifiedVariables;
    }

    public ABoolExpr getCondition() {
        return condition;
    }

    public ASubstitution getSubstitution() {
        return substitution;
    }

}
