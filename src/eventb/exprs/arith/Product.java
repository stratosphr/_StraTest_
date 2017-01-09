package eventb.exprs.arith;

import eventb.exprs.INaryOperation;
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
public final class Product extends AArithExpr implements INaryOperation {

    private final List<AArithExpr> operands;

    public Product(AArithExpr... operands) {
        if (operands.length < 2) {
            throw new Error("A product requires at least 2 operands (" + operands.length + " given).");
        }
        this.operands = Arrays.asList(operands);
    }

    @Override
    public Product clone() {
        return new Product(operands.stream().map(AArithExpr::clone).toArray(AArithExpr[]::new));
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
    public List<AArithExpr> getOperands() {
        return operands;
    }

}
