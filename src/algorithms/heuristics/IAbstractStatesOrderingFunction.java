package algorithms.heuristics;

import eventb.graphs.AbstractState;
import utilities.Tuple;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

/**
 * Created by gvoiron on 23/12/16.
 * Time : 01:43
 */
public interface IAbstractStatesOrderingFunction extends Function<Tuple<AbstractState, LinkedHashSet<AbstractState>>, List<AbstractState>> {

}
