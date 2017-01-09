package eventb.exprs.sets;

import eventb.exprs.arith.IntVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 04/01/17.
 * Time : 11:45
 */
public final class NamedSet extends ASetExpr {

    private String name;

    public NamedSet(String name) {
        this.name = name;
    }

    @Override
    public NamedSet clone() {
        throw new Error("CLONING A NAMEDSET NEEDS TO BE MODIFIED!");
    }

    @Override
    public String accept(EventBFormatter visitor) {
        throw new Error("Unhandled visit in NamedSet");
    }

    @Override
    public ASetExpr accept(Primer visitor) {
        throw new Error("Unhandled visit in NamedSet");
    }

    @Override
    public ASetExpr accept(UnPrimer visitor) {
        throw new Error("Unhandled visit in NamedSet");
    }

    @Override
    public String accept(SMTLibFormatter visitor) {
        throw new Error("Unhandled visit in NamedSet");
    }

    @Override
    public LinkedHashSet<IntVariable> getVariables() {
        throw new Error("Unhandled visit in NamedSet");
    }

    public String getName() {
        return name;
    }

}
