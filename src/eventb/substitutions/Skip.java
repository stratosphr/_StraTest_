package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.True;
import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:30
 */
public final class Skip extends ASubstitution {

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public ABoolExpr getPrd(Machine machine) {
        return new True();
    }

}
