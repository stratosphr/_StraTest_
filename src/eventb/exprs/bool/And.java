package eventb.exprs.bool;

import eventb.visitors.EventBFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:56
 */
public final class And extends ABoolExpr {

    private final List<ABoolExpr> operands;

    public And(ABoolExpr... operands) {
        this.operands = Arrays.asList(operands);
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public List<ABoolExpr> getOperands() {
        return operands;
    }

}
