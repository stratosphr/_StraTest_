package eventb.exprs;

import eventb.AEventBObject;
import eventb.visitors.IPrimerVisitable;
import eventb.visitors.ISMTLibFormatterVisitable;
import eventb.visitors.Primer;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:16
 */
public abstract class AExpr<T extends AExpr> extends AEventBObject implements IPrimerVisitable<T>, ISMTLibFormatterVisitable {

    public T prime() {
        return accept(new Primer());
    }

}
