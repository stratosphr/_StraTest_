package eventb.exprs;

import eventb.AEventBObject;
import eventb.visitors.IPrimerVisitable;
import eventb.visitors.ISMTLibFormatterVisitable;
import eventb.visitors.Primer;
import eventb.visitors.UnPrimer;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:16
 */
public abstract class AExpr<T extends AExpr> extends AEventBObject implements IPrimerVisitable<T>, ISMTLibFormatterVisitable {

    public final T prime() {
        return accept(new Primer());
    }

    public final T unPrime() {
        return accept(new UnPrimer());
    }

}
