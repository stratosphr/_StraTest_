package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;
import eventb.visitors.SMTLibFormatter;

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
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public String accept(SMTLibFormatter visitor) {
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
