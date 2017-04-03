package algorithms.statistics;

import algorithms.ChinesePostmanPathsComputer;
import algorithms.ConnectedATSComputer;
import algorithms.heuristics.EConcreteStateColor;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ComputerResult;
import algorithms.utilities.ReachableAbstractPartComputer;
import algorithms.utilities.ReachableConcretePartComputer;
import eventb.graphs.*;
import utilities.AFormatter;
import utilities.sets.Tuple;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static algorithms.heuristics.EConcreteStateColor.BLUE;
import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 09/01/17.
 * Time : 12:23
 */
public final class ATSStatistics extends AFormatter {

    private final LinkedHashMap<String, LinkedHashMap<String, Object>> statistics;

    public ATSStatistics(ApproximatedTransitionSystem ats) {
        this.statistics = new LinkedHashMap<>();
        LinkedHashMap<String, Object> abstractionStatistics = new LinkedHashMap<>();
        LinkedHashMap<String, Object> underApproximationStatistics = new LinkedHashMap<>();
        LinkedHashMap<String, Object> testsStatistics = new LinkedHashMap<>();
        Tuple<LinkedHashSet<AbstractState>, LinkedHashSet<AbstractTransition>> reachableAbstractPart = new ReachableAbstractPartComputer(ats.getCTS().getC0(), ats.getCTS().getDeltaC(), ats.getCTS().getAlpha()).compute().getResult();
        Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> reachableConcretePart = new ReachableConcretePartComputer(ats.getCTS().getC0(), ats.getCTS().getDeltaC()).compute().getResult();
        ComputerResult<ApproximatedTransitionSystem> connectedATS = new ConnectedATSComputer(ats).compute();
        ComputerResult<List<List<ConcreteTransition>>> testsResult = new ChinesePostmanPathsComputer(connectedATS.getResult().getCTS().getC0().iterator().next(), connectedATS.getResult().getCTS().getC(), connectedATS.getResult().getCTS().getDeltaC()).compute();
        List<List<ConcreteTransition>> tests = testsResult.getResult().stream().map(test -> test.stream().filter(step -> !step.getSource().getName().equals("_fictive_") && !step.getEvent().getName().equals("_beta_") && !step.getEvent().getName().equals("_reset_") && !step.getTarget().getName().equals("_fictive_")).collect(Collectors.toList())).collect(Collectors.toList());
        abstractionStatistics.put("# initial abstract states", ats.get3MTS().getQ0().size());
        abstractionStatistics.put("# abstract states", ats.get3MTS().getQ().size());
        abstractionStatistics.put("# reachable abstract states", reachableAbstractPart.getFirst().size());
        abstractionStatistics.put("% tau abstract states", 100.0 * reachableAbstractPart.getFirst().size() / ats.get3MTS().getQ().size());
        abstractionStatistics.put("# abstract transitions", ats.get3MTS().getDelta().size());
        abstractionStatistics.put("# reachable abstract transitions", reachableAbstractPart.getSecond().size());
        abstractionStatistics.put("# unreachable abstract transitions", ats.get3MTS().getDelta().size() - reachableAbstractPart.getSecond().size());
        abstractionStatistics.put("% tau abstract transitions", 100.0 * reachableAbstractPart.getSecond().size() / ats.get3MTS().getDelta().size());
        abstractionStatistics.put("# may transitions", ats.get3MTS().getDelta().size());
        abstractionStatistics.put("# pure may transitions", ats.get3MTS().getDeltaPureMay().size());
        abstractionStatistics.put("# must- transitions", ats.get3MTS().getDeltaMinus().size());
        abstractionStatistics.put("# pure must- transitions", ats.get3MTS().getDeltaPureMinus().size());
        abstractionStatistics.put("# must+ transitions", ats.get3MTS().getDeltaPlus().size());
        abstractionStatistics.put("# pure must+ transitions", ats.get3MTS().getDeltaPurePlus().size());
        abstractionStatistics.put("# must# transitions", ats.get3MTS().getDeltaSharp().size());
        if ((Integer) abstractionStatistics.get("# abstract transitions") != (Integer) abstractionStatistics.get("# pure may transitions") + (Integer) abstractionStatistics.get("# pure must- transitions") + (Integer) abstractionStatistics.get("# pure must+ transitions") + (Integer) abstractionStatistics.get("# must# transitions")) {
            System.err.println(abstractionStatistics.get("# abstract transitions"));
            System.err.println(abstractionStatistics.get("# pure may transitions"));
            System.err.println(abstractionStatistics.get("# pure must- transitions"));
            System.err.println(abstractionStatistics.get("# pure must+ transitions"));
            System.err.println(abstractionStatistics.get("# pure must# transitions"));
            throw new Error("IMPOSSIBLE");
        }
        underApproximationStatistics.put("# initial concrete states", ats.getCTS().getC0().size());
        underApproximationStatistics.put("# concrete states", ats.getCTS().getC().size());
        underApproximationStatistics.put("# reachable concrete states", reachableConcretePart.getFirst().size());
        underApproximationStatistics.put("% rho concrete states", 100.0 * reachableConcretePart.getFirst().size() / ats.getCTS().getC().size());
        underApproximationStatistics.put("# blue concrete states", ats.getCTS().getKappa().values().stream().filter(color -> color.equals(BLUE)).count());
        underApproximationStatistics.put("# green concrete states", ats.getCTS().getKappa().values().stream().filter(color -> color.equals(EConcreteStateColor.GREEN)).count());
        underApproximationStatistics.put("# concrete transitions", ats.getCTS().getDeltaC().size());
        underApproximationStatistics.put("# reachable concrete transitions", reachableConcretePart.getSecond().size());
        underApproximationStatistics.put("# unreachable concrete transitions", ats.getCTS().getDeltaC().size() - reachableConcretePart.getSecond().size());
        underApproximationStatistics.put("rho concrete transitions", 1.0 * ats.getCTS().getDeltaC().size() / reachableAbstractPart.getSecond().size());
        underApproximationStatistics.put("% covered events", 100.0 * reachableConcretePart.getSecond().stream().map(ATransition::getEvent).collect(Collectors.toCollection(LinkedHashSet::new)).size() / ats.getEventSystem().getEvDef().size());
        testsStatistics.put("# test cases", tests.size());
        testsStatistics.put("# tests lengths", tests.stream().map(List::size).collect(Collectors.toList()));
        testsStatistics.put("# tests steps", tests.stream().mapToInt(List::size).sum());
        testsStatistics.put("~ tests lengths", tests.stream().mapToInt(List::size).average().orElse(0));
        statistics.put("Abstraction", abstractionStatistics);
        statistics.put("Under-Approximation", underApproximationStatistics);
        statistics.put("Tests", testsStatistics);
    }

    public String getRowRepresentation(List<Integer> filterAndOrder) {
        List<Object> values = Stream.of(statistics.get("Abstraction").values().stream(), statistics.get("Under-Approximation").values().stream(), statistics.get("Tests").values().stream()).flatMap(stream -> stream).collect(Collectors.toList());
        List<Object> filteredAndSortedValues = new ArrayList<>();
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        filterAndOrder.forEach(index -> {
            try {
                filteredAndSortedValues.add(df.format(values.get(index)));
            } catch (IllegalArgumentException e) {
                filteredAndSortedValues.add(values.get(index));
            }
        });
        return filteredAndSortedValues.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        StringBuilder formatted = new StringBuilder();
        for (String title : statistics.keySet()) {
            formatted.append(title).append(NEW_LINE);
            indentRight();
            formatted.append(statistics.get(title).keySet().stream().map(label -> indent() + label + ": " + statistics.get(title).get(label)).collect(Collectors.joining(NEW_LINE))).append(NEW_LINE);
            indentLeft();
            formatted.append(NEW_LINE);
        }
        return formatted.toString();
    }

}
