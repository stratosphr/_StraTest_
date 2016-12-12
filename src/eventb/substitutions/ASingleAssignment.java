package eventb.substitutions;

import eventb.exprs.arith.AArithExpr;
import eventb.exprs.arith.AAssignable;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:43
 */
public abstract class ASingleAssignment extends AAssignment {

    protected final AAssignable assignable;
    private final AArithExpr value;

    public ASingleAssignment(AAssignable assignable, AArithExpr value) {
        this.assignable = assignable;
        this.value = value;
    }

    public AAssignable getAssignable() {
        return assignable;
    }

    public AArithExpr getValue() {
        return value;
    }

}
