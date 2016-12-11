package eventb.exprs.bool;

import eventb.exprs.IUnaryOperation;
import eventb.exprs.arith.IntVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.SMTLibFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:56
 */
public final class Not extends ABoolExpr implements IUnaryOperation {

    private final ABoolExpr operand;

    public Not(ABoolExpr operand) {
        this.operand = operand;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public String accept(SMTLibFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public LinkedHashSet<IntVariable> getVariables() {
        return getOperand().getVariables();
    }

    @Override
    public ABoolExpr getOperand() {
        return operand;
    }

}
