package eventb.exprs.sets;

import eventb.exprs.AExpr;

/**
 * Created by gvoiron on 04/01/17.
 * Time : 11:46
 */
public abstract class ASetExpr extends AExpr<ASetExpr> {

    @Override
    public abstract ASetExpr clone();

}
