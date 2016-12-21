package eventb;

import eventb.visitors.EventBFormatter;
import eventb.visitors.IEventBFormatterVisitable;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 19:31
 */
public abstract class AEventBObject implements IEventBFormatterVisitable, Comparable<AEventBObject> {

    @Override
    public int hashCode() {
        return (getClass() + "_" + toString()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return getClass().equals(o.getClass()) && ((this == o) || toString().equals(o.toString()));
    }

    @Override
    public final String toString() {
        return accept(new EventBFormatter());
    }

    @Override
    public int compareTo(AEventBObject eventBObject) {
        return toString().compareTo(eventBObject.toString());
    }

}
