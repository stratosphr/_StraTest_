package algorithms.outputs;

import eventb.Event;
import eventb.exprs.arith.AAssignable;
import eventb.exprs.bool.Invariant;
import eventb.substitutions.ASubstitution;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:42
 */
public final class EventSystem {

    private final LinkedHashSet<AAssignable> X;
    private final Invariant I;
    private final ASubstitution Init;
    private final LinkedHashSet<Event> EvDef;

    public EventSystem(LinkedHashSet<AAssignable> X, Invariant I, ASubstitution Init, LinkedHashSet<Event> EvDef) {
        this.X = X;
        this.I = I;
        this.Init = Init;
        this.EvDef = EvDef;
    }

    public LinkedHashSet<AAssignable> getX() {
        return X;
    }

    public Invariant getI() {
        return I;
    }

    public ASubstitution getInit() {
        return Init;
    }

    public LinkedHashSet<Event> getEvDef() {
        return EvDef;
    }

}
