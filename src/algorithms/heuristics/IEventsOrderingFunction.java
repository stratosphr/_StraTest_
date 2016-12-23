package algorithms.heuristics;

import eventb.Event;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

/**
 * Created by gvoiron on 23/12/16.
 * Time : 01:43
 */
public interface IEventsOrderingFunction extends Function<LinkedHashSet<Event>, List<Event>> {

}
