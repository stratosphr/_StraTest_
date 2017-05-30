package algorithms.utilities;

import algorithms.AComputer;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import utilities.sets.Tuple;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 10/04/17.
 * Time : 22:18
 */
public class AbstractSimplePathsComputer extends AComputer<List<Tuple<List<AbstractState>, List<AbstractTransition>>>> {

    private LinkedHashSet<AbstractState> vertices;
    private final LinkedHashSet<AbstractState> initialVertices;
    private LinkedHashSet<AbstractTransition> edges;
    private LinkedHashMap<AbstractState, LinkedHashSet<AbstractTransition>> adjacencyMatrix;

    public AbstractSimplePathsComputer(LinkedHashSet<AbstractState> initialVertices, LinkedHashSet<AbstractState> vertices, LinkedHashSet<AbstractTransition> edges) {
        this.vertices = vertices;
        this.initialVertices = initialVertices;
        this.edges = edges;
    }

    @Override
    protected List<Tuple<List<AbstractState>, List<AbstractTransition>>> compute_() {
        this.adjacencyMatrix = new LinkedHashMap<>();
        for (AbstractState vertex : vertices) {
            adjacencyMatrix.put(vertex, edges.stream().filter(abstractTransition -> abstractTransition.getSource().equals(vertex)).collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        List<LinkedHashSet<AbstractTransition>> paths = new ArrayList<>();
        /*adjacencyMatrix.forEach((abstractState, abstractTransitions) -> {
            System.out.println(abstractState + " : " + NEW_LINE + TABULATION + abstractTransitions.stream().map(ATransition::toString).collect(Collectors.joining(NEW_LINE + TABULATION)) + NEW_LINE);
        });*/
        for (AbstractState initialVertex : initialVertices) {
            recursion(initialVertex, new LinkedHashSet<>(), paths);
        }
        paths.forEach(path -> {
            path.forEach(concreteTransition -> System.out.println(concreteTransition.getSource().getName() + " --[ " + concreteTransition.getEvent().getName() + " ]-> " + concreteTransition.getTarget().getName()));
            System.out.println("---------------------------------------------------------------------------");
        });
        return null;
    }

    private void recursion(AbstractState initialVertex, LinkedHashSet<AbstractTransition> path, List<LinkedHashSet<AbstractTransition>> paths) {
        LinkedHashSet<AbstractTransition> tmpAdjacent = new LinkedHashSet<>(adjacencyMatrix.get(initialVertex));
        LinkedHashSet<AbstractTransition> tmpPath;
        AbstractTransition nextEdge = adjacencyMatrix.get(initialVertex).stream().filter(abstractTransition -> !path.contains(abstractTransition)).findFirst().orElse(null);
        if (nextEdge == null) {
            paths.add(path);
        } else {
            tmpAdjacent.remove(nextEdge);
            LinkedHashSet<AbstractTransition> branchings = new LinkedHashSet<>(tmpAdjacent);
            tmpPath = new LinkedHashSet<>(path);
            if (path.add(nextEdge)) {
                recursion(nextEdge.getTarget(), path, paths);
            } else {
                paths.add(path);
            }
            for (AbstractTransition branching : branchings) {
                if (!tmpPath.contains(branching)) {
                    LinkedHashSet<AbstractTransition> tmpPath2 = new LinkedHashSet<>(tmpPath);
                    tmpPath2.add(branching);
                    recursion(branching.getTarget(), new LinkedHashSet<>(tmpPath2), paths);
                }
            }
        }
    }

}
