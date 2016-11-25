package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:56
 */
public final class Subtraction extends AArithExpr {

    private final List<AArithExpr> operands;

    public Subtraction(AArithExpr... operands) {
        if (operands.length < 2) {
            throw new Error("A subtraction requires at least 2 operands (" + operands.length + " given).");
        }
        this.operands = Arrays.asList(operands);
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public List<AArithExpr> getOperands() {
        return operands;
    }

}
