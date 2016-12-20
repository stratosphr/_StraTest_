package eventb.graphs;

import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Not;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.TreeMap;

/**
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public final class AbstractState extends AState<ABoolExpr, Boolean> {

    public AbstractState(String name, TreeMap<ABoolExpr, Boolean> mapping) {
        super(name, new And(mapping.keySet().stream().map(predicate -> mapping.get(predicate) ? predicate : new Not(predicate)).toArray(ABoolExpr[]::new)), mapping);
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

}
