package eventb;

import eventb.substitutions.ASubstitution;
import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:34
 */
public final class Event extends AEventBObject implements Comparable<Event> {

    private final String name;
    private final ASubstitution substitution;

    public Event(String name, ASubstitution substitution) {
        this.name = name;
        this.substitution = substitution;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public ASubstitution getSubstitution() {
        return substitution;
    }

    @Override
    public int compareTo(Event event) {
        return toString().compareTo(event.toString());
    }

}
