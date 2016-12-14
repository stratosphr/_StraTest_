package graphs.eventb;

import eventb.exprs.arith.Int;
import eventb.exprs.arith.IntVariable;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Equals;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.TreeMap;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:07
 */
public final class ConcreteState extends AState {

    private final TreeMap<IntVariable, Int> state;

    public ConcreteState(String name, TreeMap<IntVariable, Int> state) {
        super(name, new And(state.keySet().stream().map(intVariable -> new Equals(intVariable, state.get(intVariable))).toArray(ABoolExpr[]::new)));
        this.state = state;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
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
    public String accept(SMTLibFormatter visitor) {
        return visitor.visit(this);
    }

    public TreeMap<IntVariable, Int> getState() {
        return state;
    }

}
