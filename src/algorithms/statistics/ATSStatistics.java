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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        abstractionStatistics.put("% tau abstract states", 100.0 * reachableAbstractPart.getFirst().size() / approximatedTransitionSystem.getTriModalTransitionSystem().getQ().size());
        abstractionStatistics.put("# abstract transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDelta().size());
        abstractionStatistics.put("# reachable abstract transitions", reachableAbstractPart.getSecond().size());
        abstractionStatistics.put("% tau abstract transitions", 100.0 * reachableAbstractPart.getSecond().size() / approximatedTransitionSystem.getTriModalTransitionSystem().getDelta().size());
        abstractionStatistics.put("# pure may transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPureMay().size());
        abstractionStatistics.put("# pure must- transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPureMinus().size());
        abstractionStatistics.put("# pure must+ transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPurePlus().size());
        abstractionStatistics.put("# must# transitions", approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaSharp().size());
        if ((Integer) abstractionStatistics.get("# abstract transitions") != (Integer) abstractionStatistics.get("# pure may transitions") + (Integer) abstractionStatistics.get("# pure must- transitions") + (Integer) abstractionStatistics.get("# pure must+ transitions") + (Integer) abstractionStatistics.get("# must# transitions")) {
            System.err.println(abstractionStatistics.get("# abstract transitions"));
            System.err.println(abstractionStatistics.get("# pure may transitions"));
            System.err.println(abstractionStatistics.get("# pure must- transitions"));
            System.err.println(abstractionStatistics.get("# pure must+ transitions"));
            System.err.println(abstractionStatistics.get("# pure must# transitions"));
            throw new Error("IMPOSSIBLE");
        }
        underApproximationStatistics.put("# initial concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getC0().size());
        underApproximationStatistics.put("# concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getC().size());
        underApproximationStatistics.put("# reachable concrete states", reachableConcretePart.getFirst().size());
        underApproximationStatistics.put("% rho concrete states", 100.0 * reachableConcretePart.getFirst().size() / approximatedTransitionSystem.getConcreteTransitionSystem().getC().size());
        underApproximationStatistics.put("# blue concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().values().stream().filter(color -> color.equals(EConcreteStateColor.BLUE)).count());
        underApproximationStatistics.put("# green concrete states", approximatedTransitionSystem.getConcreteTransitionSystem().getKappa().values().stream().filter(color -> color.equals(EConcreteStateColor.GREEN)).count());
        underApproximationStatistics.put("# concrete transitions", approximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().size());
        underApproximationStatistics.put("# reachable concrete transitions", reachableConcretePart.getSecond().size());
        underApproximationStatistics.put("% rho concrete transitions", 1.0 * approximatedTransitionSystem.getConcreteTransitionSystem().getDeltaC().size() / reachableAbstractPart.getSecond().size());
        statistics.put("Abstraction", abstractionStatistics);
        statistics.put("Under-Approximation", underApproximationStatistics);
    }

    public String getRowRepresentation(List<Integer> filterAndOrder) {
        List<Object> values = Stream.concat(statistics.get("Abstraction").values().stream(), statistics.get("Under-Approximation").values().stream()).collect(Collectors.toList());
        List<Object> filteredAndSortedValues = new ArrayList<>();
        filterAndOrder.forEach(index -> filteredAndSortedValues.add(values.get(index)));
        return filteredAndSortedValues.stream().map(Object::toString).collect(Collectors.joining(" "));
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
