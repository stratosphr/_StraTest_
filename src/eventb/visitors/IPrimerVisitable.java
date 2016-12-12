package eventb.visitors;

import eventb.exprs.AExpr;

/**
 * Created by gvoiron on 12/12/16.
 * Time : 14:02
 */
public interface IPrimerVisitable<T extends AExpr> {

    T accept(Primer visitor);

}
