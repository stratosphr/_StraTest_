package eventb.graphs;

import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.APredicate;

import java.util.TreeMap;

/**
 * Created by gvoiron on 20/12/16.
 * Time : 18:41
 */
public abstract class AState<Domain, CoDomain> extends APredicate {

    private final TreeMap<Domain, CoDomain> mapping;

    public AState(String name, ABoolExpr expression, TreeMap<Domain, CoDomain> mapping) {
        super(name, expression);
        this.mapping = mapping;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public ABoolExpr getExpression() {
        return super.getExpression();
    }

    public TreeMap<Domain, CoDomain> getMapping() {
        return mapping;
    }

}
