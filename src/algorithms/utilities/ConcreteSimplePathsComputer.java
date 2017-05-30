package algorithms.utilities;

import algorithms.AComputer;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;
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
public class ConcreteSimplePathsComputer extends AComputer<List<Tuple<List<ConcreteState>, List<ConcreteTransition>>>> {

    private LinkedHashSet<ConcreteState> vertices;
    private final LinkedHashSet<ConcreteState> initialVertices;
    private LinkedHashSet<ConcreteTransition> edges;
    private LinkedHashMap<ConcreteState, LinkedHashSet<ConcreteTransition>> adjacencyMatrix;

    public ConcreteSimplePathsComputer(LinkedHashSet<ConcreteState> initialVertices, LinkedHashSet<ConcreteTransition> edges) {
        Tuple<LinkedHashSet<ConcreteState>, LinkedHashSet<ConcreteTransition>> reachablePart = new ReachableConcretePartComputer(initialVertices, edges).compute().getResult();
        this.vertices = reachablePart.getFirst();
        this.initialVertices = initialVertices.stream().filter(vertex -> vertices.contains(vertex)).collect(Collectors.toCollection(LinkedHashSet::new));
        this.edges = reachablePart.getSecond();
    }

    @Override
    protected List<Tuple<List<ConcreteState>, List<ConcreteTransition>>> compute_() {
        this.adjacencyMatrix = new LinkedHashMap<>();
        for (ConcreteState vertex : vertices) {
            adjacencyMatrix.put(vertex, edges.stream().filter(concreteTransition -> concreteTransition.getSource().equals(vertex)).collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        List<LinkedHashSet<ConcreteTransition>> paths = new ArrayList<>();
        for (ConcreteState initialVertex : initialVertices) {
            recursion(initialVertex, new LinkedHashSet<>(), paths);
        }
        paths.forEach(path -> {
            path.forEach(concreteTransition -> System.out.println(concreteTransition.getSource().getName() + " --[ " + concreteTransition.getEvent().getName() + " ]-> " + concreteTransition.getTarget().getName()));
            System.out.println("---------------------------------------------------------------------------");
        });
        return null;
    }

    private void recursion(ConcreteState initialVertex, LinkedHashSet<ConcreteTransition> path, List<LinkedHashSet<ConcreteTransition>> paths) {
        LinkedHashSet<ConcreteTransition> tmpAdjacent = new LinkedHashSet<>(adjacencyMatrix.get(initialVertex));
        LinkedHashSet<ConcreteTransition> tmpPath;
        ConcreteTransition nextEdge = adjacencyMatrix.get(initialVertex).stream().filter(concreteTransition -> !path.contains(concreteTransition)).findFirst().orElse(null);
        if (nextEdge == null) {
            paths.add(path);
        } else {
            tmpAdjacent.remove(nextEdge);
            LinkedHashSet<ConcreteTransition> branchings = new LinkedHashSet<>(tmpAdjacent);
            tmpPath = new LinkedHashSet<>(path);
            if (path.add(nextEdge)) {
                recursion(nextEdge.getTarget(), path, paths);
            } else {
                paths.add(path);
            }
            for (ConcreteTransition branching : branchings) {
                if (!tmpPath.contains(branching)) {
                    LinkedHashSet<ConcreteTransition> tmpPath2 = new LinkedHashSet<>(tmpPath);
                    tmpPath2.add(branching);
                    recursion(branching.getTarget(), new LinkedHashSet<>(tmpPath2), paths);
                }
            }
        }
    }

}
