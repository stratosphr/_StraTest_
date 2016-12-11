package eventb.exprs.arith;

import eventb.visitors.EventBFormatter;
import eventb.visitors.SMTLibFormatter;

import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:19
 */
public final class IntVariable extends AAssignable {

    public IntVariable(String name) {
        super(name);
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
        return new LinkedHashSet<>(Collections.singletonList(this));
    }

}
