package graphs.eventb;

import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:07
 */
public final class AbstractState extends AState {

    private final LinkedHashSet<ABoolExpr> predicates;

    public AbstractState(String name, LinkedHashSet<ABoolExpr> predicates) {
        super(name, new And(predicates.stream().toArray(ABoolExpr[]::new)));
        this.predicates = predicates;
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

    public LinkedHashSet<ABoolExpr> getPredicates() {
        return predicates;
    }

}
