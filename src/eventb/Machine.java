package eventb;

import eventb.events.ASubstitution;
import eventb.events.Event;
import eventb.exprs.arith.AAssignable;
import eventb.exprs.bool.Invariant;
import eventb.visitors.EventBFormatter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by gvoiron on 28/11/16.
 * Time : 11:41
 */
public final class Machine extends AEventBObject {

    private final String name;
    private final LinkedHashSet<Object> sets;
    private final Set<AAssignable> variables;
    private final Invariant invariant;
    private final ASubstitution initialisation;
    private final Set<Event> events;

    public Machine(String name, LinkedHashSet<Object> sets, Set<AAssignable> variables, Invariant invariant, ASubstitution initialisation, LinkedHashSet<Event> events) {
        if (!sets.isEmpty()) {
            throw new Error("Sets are not yet handled.");
        }
        this.name = name;
        this.sets = sets;
        this.variables = variables;
        this.invariant = invariant;
        this.initialisation = initialisation;
        this.events = events;
    }

    public String getName() {
        return name;
    }

    public LinkedHashSet<Object> getSets() {
        return sets;
    }

    public Set<AAssignable> getVariables() {
        return variables;
    }

    public Invariant getInvariant() {
        return invariant;
    }

    public ASubstitution getInitialisation() {
        return initialisation;
    }

    public Set<Event> getEvents() {
        return events;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

}
