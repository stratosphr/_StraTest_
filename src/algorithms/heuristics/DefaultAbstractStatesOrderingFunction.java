package algorithms.heuristics;

import eventb.graphs.AbstractState;
import utilities.sets.Tuple;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gvoiron on 23/12/16.
 * Time : 01:50
 */
public final class DefaultAbstractStatesOrderingFunction implements IAbstractStatesOrderingFunction {

    @Override
    public List<AbstractState> apply(Tuple<AbstractState, LinkedHashSet<AbstractState>> tuple) {
        return new ArrayList<>(Stream.of(Collections.singletonList(tuple.getFirst()), tuple.getSecond()).flatMap(Collection::stream).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

}
