package eventb.exprs.bool;

import eventb.exprs.arith.AArithExpr;
import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:29
 */
public final class GreaterThan extends ABoolExpr {

    private final AArithExpr left;
    private final AArithExpr right;

    public GreaterThan(AArithExpr left, AArithExpr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public AArithExpr getLeft() {
        return left;
    }

    public AArithExpr getRight() {
        return right;
    }

}
