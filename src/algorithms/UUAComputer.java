package algorithms;

import algorithms.outputs.ApproximatedTransitionSystem;
import eventb.graphs.AbstractTransition;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:36
 */
public final class UUAComputer implements IComputer<ApproximatedTransitionSystem> {

    private final ApproximatedTransitionSystem approximatedTransitionSystem;
    private final LinkedHashMap<AbstractTransition, Boolean> MinusMarking;
    private final LinkedHashMap<AbstractTransition, Boolean> PlusMarking;

    public UUAComputer(ApproximatedTransitionSystem approximatedTransitionSystem) {
        this.approximatedTransitionSystem = approximatedTransitionSystem;
        this.MinusMarking = new LinkedHashMap<>(approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaMinus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
        this.PlusMarking = new LinkedHashMap<>(approximatedTransitionSystem.getTriModalTransitionSystem().getDeltaPlus().stream().map(abstractTransition -> new AbstractMap.SimpleEntry<>(abstractTransition, false)).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
    }

    @Override
    public ApproximatedTransitionSystem compute() {
        MinusMarking.forEach((abstractTransition, aBoolean) -> System.out.println(abstractTransition + ": " + aBoolean));
        return null;
    }

}
