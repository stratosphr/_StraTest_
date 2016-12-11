package eventb.exprs.bool;

import eventb.exprs.arith.IntVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.SMTLibFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 28/11/16.
 * Time : 08:59
 */
public final class False extends ABoolExpr {

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

}
