package eventb.exprs.bool;

import eventb.exprs.INaryOperation;
import eventb.exprs.arith.IntVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.SMTLibFormatter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:56
 */
public final class And extends ABoolExpr implements INaryOperation {

    private final List<ABoolExpr> operands;

    public And(ABoolExpr... operands) {
        this.operands = Arrays.asList(operands);
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
        return getOperands().stream().flatMap(operand -> operand.getVariables().stream()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public List<ABoolExpr> getOperands() {
        return operands;
    }

}
