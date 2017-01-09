package algorithms.statistics;

import algorithms.heuristics.EConcreteStateColor;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.utilities.ReachableAbstractPartComputer;
import algorithms.utilities.ReachableConcretePartComputer;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
import utilities.AFormatter;
import utilities.sets.Tuple;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 09/01/17.
 * Time : 12:23
 */
public final class ATSStatistics extends AFormatter {

    private final LinkedHashMap<String, LinkedHashMap<String, Object>> statistics;

    public ATSStatistics(ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.statistics = new LinkedHashMap<>();
        LinkedHashMap<String, Object> abstractionStatistics = new LinkedHashMap<>();
        LinkedHashMap<String, Object> underApproximationStatistics = new LinkedHashMap<>();
        Tuple<LinkedHashSet<AbstractState>, LinkedHashSet<AbstractTransition>> reachableAbstractPart = new ReachableAbstractPartComputer(approximatedTransitionSystem.getConcreteTransitionSystem().getC0(), approximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC(), approximatedTransitionSystem.getConcreteTransitionSystem().getAlpha()).compute().getResult();
        Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> reachableConcretePart = new ReachableConcretePartComputer(approximatedTransitionSystem.getConcreteTransitionSystem().getC0(), approximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC()).compute().getResult();
        abstractionStatistics.put("# initial abstract states", approximatedTransitionSystem.getTriModalTransitionSystem().getQ0().size());
        abstractionStatistics.put("# abstract states", approximatedTransitionSystem.getTriModalTransitionSystem().getQ().size());
        abstractionStatistics.put("# reachable abstract states", reachableAbstractPart.getFirst().size());
        abstractionStatistics.put("# abstract transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDelta().size());
        abstractionStatistics.put("# reachable abstract transitions", reachableAbstractPart.getSecond().size());
        abstractionStatistics.put("# pure may transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPureMay().size());
        abstractionStatistics.put("# pure must- transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPureMinus().size());
        abstractionStatistics.put("# pure must+ transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPurePlus().size());
        abstractionStatistics.put("# must# transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaSharp().size());
        underApproximationStatistics.put("# initial concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getC0().size());
        underApproximationStatistics.put("# concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getC().size());
        underApproximationStatistics.put("# reachable concrete states", reachableConcretePart.getFirst().size());
        underApproximationStatistics.put("# blue concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().values().stream().filter(color -> color.equals(EConcreteStateColor.BLUE)).count());
        underApproximationStatistics.put("# green concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().values().stream().filter(color -> color.equals(EConcreteStateColor.GREEN)).count());
        underApproximationStatistics.put("# concrete transitions", approximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().size());
        underApproximationStatistics.put("# reachable concrete transitions", reachableConcretePart.getSecond().size());
        statistics.put("Abstraction", abstractionStatistics);
        statistics.put("Under-Approximation", underApproximationStatistics);
    }

    @Override
    public String toString() {
        String formatted = "";
        for (String title : statistics.keySet()) {
            formatted += title + NEW_LINE;
            indentRight();
            formatted += statistics.get(title).keySet().stream().map(label -> indent() + label + ": " + statistics.get(title).get(label)).collect(Collectors.joining(NEW_LINE)) + NEW_LINE;
            indentLeft();
            formatted += NEW_LINE;
        }
        return formatted;
    }

}
