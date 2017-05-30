package algorithms.statistics;

import algorithms.AComputer;
import eventb.graphs.AbstractState;
import eventb.graphs.AbstractTransition;
import utilities.sets.Tuple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 20/04/17.
 * Time : 13:43
 */
public final class SimplePathsComputer<V extends AbstractState, T extends AbstractTransition> extends AComputer<List<LinkedHashSet<T>>> {

    private final Tuple<Set<V>, List<T>> graph;
    private final V startVertex;
    private final V endVertex;
    private final Map<V, List<T>> adjacency;

    public SimplePathsComputer(Tuple<Set<V>, List<T>> graph, V startVertex) {
        this(graph, startVertex, null);
    }

    public SimplePathsComputer(Tuple<Set<V>, List<T>> graph, V startVertex, V endVertex) {
        this.graph = graph;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.adjacency = new LinkedHashMap<>();
        graph.getSecond().forEach(edge -> {
            graph.getFirst().add((V) edge.getSource());
            graph.getFirst().add((V) edge.getTarget());
            adjacency.putIfAbsent((V) edge.getSource(), new ArrayList<>());
            adjacency.putIfAbsent((V) edge.getTarget(), new ArrayList<>());
            adjacency.get(edge.getSource()).add(edge);
        });
    }

    @Override
    protected List<LinkedHashSet<T>> compute_() {
        List<LinkedHashSet<T>> paths = new ArrayList<>();
        if (graph.getFirst().contains(startVertex)) {
            if (endVertex != null) {
                compute_(startVertex, endVertex, new LinkedHashSet<>(), paths);
            } else {
                compute_(startVertex, new LinkedHashSet<>(), paths);
            }
        }
        return paths;
    }

    private void compute_(V startVertex, LinkedHashSet<T> path, List<LinkedHashSet<T>> paths) {
        Set<T> adjacentEdges = adjacency.get(startVertex).stream().filter(edge -> !path.contains(edge)).collect(Collectors.toSet());
        if (adjacentEdges.isEmpty() && !path.isEmpty()) {
            paths.add(path);
        } else {
            adjacentEdges.forEach(adjacentEdge -> {
                LinkedHashSet<T> newPath = new LinkedHashSet<>(path);
                newPath.add(adjacentEdge);
                compute_((V) adjacentEdge.getTarget(), newPath, paths);
            });
        }
    }

    private void compute_(V startVertex, V endVertex, LinkedHashSet<T> path, List<LinkedHashSet<T>> paths) {
        if (endVertex == startVertex && !path.isEmpty()) {
            paths.add(path);
        } else {
            Set<T> adjacentEdges = adjacency.get(startVertex).stream().filter(edge -> !path.contains(edge)).collect(Collectors.toSet());
            if (!adjacentEdges.isEmpty()) {
                adjacentEdges.forEach(adjacentEdge -> {
                    LinkedHashSet<T> newPath = new LinkedHashSet<>(path);
                    newPath.add(adjacentEdge);
                    compute_((V) adjacentEdge.getTarget(), endVertex, newPath, paths);
                });
            }
        }
    }

}
