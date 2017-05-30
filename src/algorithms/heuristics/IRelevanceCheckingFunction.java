package algorithms.heuristics;

import eventb.graphs.ConcreteState;
import utilities.sets.Tuple;

import java.util.function.Function;

/**
 * Created by gvoiron on 08/04/17.
 * Time : 17:18
 */
public interface IRelevanceCheckingFunction extends Function<Tuple<ConcreteState, ConcreteState>, Boolean> {
}
