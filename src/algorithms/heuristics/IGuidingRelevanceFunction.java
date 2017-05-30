package algorithms.heuristics;

import eventb.exprs.bool.ABoolExpr;
import eventb.graphs.ConcreteState;

import java.util.List;
import java.util.function.Function;

/**
 * Created by gvoiron on 09/04/17.
 * Time : 15:31
 */
public interface IGuidingRelevanceFunction extends Function<ConcreteState, List<ABoolExpr>> {

}
