package eventb.exprs.bool;

import eventb.exprs.INaryOperation;
import eventb.exprs.arith.IntVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

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
    public And clone() {
        return new And(operands.stream().map(ABoolExpr::clone).toArray(ABoolExpr[]::new));
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

    @Override
    public ABoolExpr accept(Primer visitor) {
        return visitor.visit(this);
    }

    @Override
    public ABoolExpr accept(UnPrimer visitor) {
        return visitor.visit(this);
    }

}
