package eventb.substitutions;

import eventb.AEventBObject;
import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:35
 */
public abstract class ASubstitution extends AEventBObject {

    public abstract ABoolExpr getPrd(Machine machine);

    @Override
    public abstract ASubstitution clone();

}
