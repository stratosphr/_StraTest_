package eventb.exprs.bool;

import eventb.exprs.arith.QuantifiedVariable;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 12/12/16.
 * Time : 16:08
 */
public abstract class AQuantifier extends ABoolExpr {

    private final ABoolExpr expression;
    private final LinkedHashSet<QuantifiedVariable> quantifiedVariables;

    public AQuantifier(ABoolExpr expression, QuantifiedVariable... quantifiedVariables) {
        if (quantifiedVariables.length == 0) {
            throw new Error("A quantifier requires at least 1 quantified variable (none given).");
        }
        this.expression = expression;
        this.quantifiedVariables = new LinkedHashSet<>(Arrays.asList(quantifiedVariables));
    }

    public ABoolExpr getExpression() {
        return expression;
    }

    public LinkedHashSet<QuantifiedVariable> getQuantifiedVariables() {
        return quantifiedVariables;
    }

}
