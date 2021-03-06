package algorithms.outputs;

import eventb.Event;
import eventb.exprs.arith.AAssignable;
import eventb.exprs.bool.Invariant;
import eventb.substitutions.ASubstitution;
import utilities.ICloneable;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:42
 */
public final class EventSystem implements ICloneable<EventSystem> {

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

    @Override
    public EventSystem clone() {
        return new EventSystem(new LinkedHashSet<>(getX()), new Invariant(getI().getExpression()), getInit().clone(), new LinkedHashSet<>(getEvDef()));
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
