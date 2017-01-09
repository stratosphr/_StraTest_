package eventb.graphs;

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
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public final class ConcreteState extends AState<IntVariable, Int> {

    public ConcreteState(String name, TreeMap<IntVariable, Int> mapping) {
        super(name, new And(mapping.keySet().stream().map(intVariable -> new Equals(intVariable, mapping.get(intVariable))).toArray(ABoolExpr[]::new)), mapping);
    }

    @Override
    public ConcreteState clone() {
        return new ConcreteState(getName(), getMapping());
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
