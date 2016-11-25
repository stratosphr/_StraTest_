package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:56
 */
public final class Sum extends AArithExpr {

    private final List<AArithExpr> operands;

    public Sum(AArithExpr... operands) {
        if (operands.length < 2) {
            throw new Error("A sum requires at least 2 operands (" + operands.length + " given).");
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
