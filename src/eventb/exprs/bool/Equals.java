package eventb.exprs.bool;

import eventb.exprs.IBinaryOperation;
import eventb.exprs.arith.AArithExpr;
import eventb.exprs.arith.IntVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:29
 */
public final class Equals extends ABoolExpr implements IBinaryOperation {

    private final AArithExpr left;
    private final AArithExpr right;

    public Equals(AArithExpr left, AArithExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public ABoolExpr accept(Primer visitor) {
        return visitor.visit(this);
    }

    @Override
    public ABoolExpr accept(UnPrimer visitor) {
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
        return Stream.concat(getLeft().getVariables().stream(), getRight().getVariables().stream()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public AArithExpr getLeft() {
        return left;
    }

    @Override
    public AArithExpr getRight() {
        return right;
    }

}
