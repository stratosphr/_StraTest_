package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.arith.AArithExpr;
import eventb.exprs.arith.IntVariable;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.Equals;
import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:43
 */
public final class VariableAssignment extends ASingleAssignment {

    public VariableAssignment(IntVariable intVariable, AArithExpr value) {
        super(intVariable, value);
    }

    @Override
    public ABoolExpr getPrd(Machine machine) {
        return new Equals(getAssignable().prime(), getValue());
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public IntVariable getAssignable() {
        return (IntVariable) assignable;
    }

}
