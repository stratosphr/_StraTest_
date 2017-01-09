package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 12/12/16.
 * Time : 15:20
 */
public final class QuantifiedVariable extends AArithExpr {

    private IntVariable variable;

    public QuantifiedVariable(IntVariable variable) {
        this.variable = variable;
    }

    @Override
    public QuantifiedVariable clone() {
        return new QuantifiedVariable(getVariable().clone());
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public AArithExpr accept(Primer visitor) {
        return visitor.visit(this);
    }

    @Override
    public AArithExpr accept(UnPrimer visitor) {
        return visitor.visit(this);
    }

    @Override
    public String accept(SMTLibFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public LinkedHashSet<IntVariable> getVariables() {
        return getVariable().getVariables();
    }

    public String getName() {
        return getVariable().getName();
    }

    public IntVariable getVariable() {
        return variable;
    }

}
