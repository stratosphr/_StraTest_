package eventb;

import eventb.exprs.arith.AAssignable;
import eventb.exprs.arith.QuantifiedVariable;
import eventb.exprs.bool.Invariant;
import eventb.substitutions.ASubstitution;
import eventb.visitors.EventBFormatter;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static eventb.parsers.metamodel.EventBRegex.IDENTIFIER;

/**
 * Created by gvoiron on 28/11/16.
 * Time : 11:41
 */
public final class Machine extends AEventBObject {

    private final String name;
    private final LinkedHashSet<Object> sets;
    private final LinkedHashSet<AAssignable> variables;
    private final Invariant invariant;
    private final ASubstitution initialisation;
    private final LinkedHashSet<Event> events;
    private QuantifiedVariable quantifiedVariables;

    public Machine(String name, LinkedHashSet<Object> sets, LinkedHashSet<AAssignable> assignables, Invariant invariant, ASubstitution initialisation, LinkedHashSet<Event> events) throws Error {
        if (!name.matches(IDENTIFIER)) {
            throw new Error("The name of a machine must match the regular expression \"" + IDENTIFIER + "\" (\"" + name + "\" given).");
        }
        if (!sets.isEmpty()) {
            throw new Error("Sets are not yet handled.");
        }
        this.name = name;
        this.sets = sets;
        this.variables = assignables;
        this.invariant = invariant;
        this.initialisation = initialisation;
        this.events = events;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public LinkedHashSet<Object> getSets() {
        return sets;
    }

    public LinkedHashSet<AAssignable> getAssignables() {
        return variables;
    }

    public Invariant getInvariant() {
        return invariant;
    }

    public ASubstitution getInitialisation() {
        return initialisation;
    }

    public LinkedHashSet<Event> getEvents() {
        return events;
    }

    public LinkedHashSet<QuantifiedVariable> getQuantifiedVariables() {
        return getAssignables().stream().flatMap(assignable -> assignable.getVariables().stream()).map(QuantifiedVariable::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
