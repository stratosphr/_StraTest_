package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:41
 */
public final class Int extends AArithExpr {

    private final Integer value;

    public Int(Integer value) {
        this.value = value;
    }

    @Override
    public Int clone() {
        return new Int(getValue());
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
    public AArithExpr accept(Primer visitor) {
        return visitor.visit(this);
    }

    @Override
    public AArithExpr accept(UnPrimer visitor) {
        return visitor.visit(this);
    }

    @Override
    public LinkedHashSet<IntVariable> getVariables() {
        return new LinkedHashSet<>();
    }

    public Integer getValue() {
        return value;
    }

}
