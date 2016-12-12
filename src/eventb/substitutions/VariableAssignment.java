package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.arith.AArithExpr;
import eventb.exprs.arith.IntVariable;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Equals;
import eventb.visitors.EventBFormatter;

import java.util.stream.Stream;

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
        return getPrd_(machine, false);
    }

    @Override
    protected ABoolExpr getPrd_(Machine machine, boolean isInMultipleAssignment) {
        return isInMultipleAssignment ? new Equals(getAssignable().prime(), getValue()) : new And(Stream.concat(machine.getAssignables().stream().filter(assignable -> !assignable.equals(getAssignable())).map(assignable -> new Equals(assignable.prime(), assignable)), Stream.of(new Equals(getAssignable().prime(), getValue()))).toArray(ABoolExpr[]::new));
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
