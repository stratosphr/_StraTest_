package algorithms.heuristics;

import eventb.Event;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by gvoiron on 23/12/16.
 * Time : 01:49
 */
public final class DefaultEventsOrderingFunction implements IEventsOrderingFunction {

    @Override
    public List<Event> apply(LinkedHashSet<Event> events) {
        return new ArrayList<>(events);
    }

}
