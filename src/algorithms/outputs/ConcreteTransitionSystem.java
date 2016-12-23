package algorithms.outputs;

import algorithms.heuristics.EConcreteStateColor;
import eventb.graphs.AbstractState;
import eventb.graphs.ConcreteState;
import eventb.graphs.ConcreteTransition;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 22/12/16.
 * Time : 13:45
 */
public final class ConcreteTransitionSystem {

    private final LinkedHashSet<ConcreteState> C0;
    private final LinkedHashSet<ConcreteState> C;
    private final LinkedHashMap<ConcreteState, AbstractState> Alpha;
    private final LinkedHashMap<ConcreteState, EConcreteStateColor> Kappa;
    private final LinkedHashSet<ConcreteTransition> DeltaC;

    public ConcreteTransitionSystem(LinkedHashSet<ConcreteState> C0, LinkedHashSet<ConcreteState> C, LinkedHashMap<ConcreteState, AbstractState> Alpha, LinkedHashMap<ConcreteState, EConcreteStateColor> Kappa, LinkedHashSet<ConcreteTransition> DeltaC) {
        this.C0 = C0;
        this.C = C;
        this.Alpha = Alpha;
        this.Kappa = Kappa;
        this.DeltaC = DeltaC;
    }

    public LinkedHashSet<ConcreteState> getC0() {
        return C0;
    }

    public LinkedHashSet<ConcreteState> getC() {
        return C;
    }

    public LinkedHashMap<ConcreteState, AbstractState> getAlpha() {
        return Alpha;
    }

    public LinkedHashMap<ConcreteState, EConcreteStateColor> getKappa() {
        return Kappa;
    }

    public LinkedHashSet<ConcreteTransition> getDeltaC() {
        return DeltaC;
    }

}
