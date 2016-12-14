package graphs.eventb;

import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.APredicate;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:07
 */
public abstract class AState extends APredicate {

    public AState(String name, ABoolExpr expression) {
        super(name, expression);
    }

}
