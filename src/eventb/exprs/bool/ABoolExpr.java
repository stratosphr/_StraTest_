package eventb.exprs.bool;

import eventb.exprs.AExpr;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:17
 */
public abstract class ABoolExpr extends AExpr<ABoolExpr> {

    @Override
    public abstract ABoolExpr clone();

}
