package algorithms.utilities;

import algorithms.visitors.DOTFormatter;
import algorithms.visitors.IDOTFormatterVisitable;
import graphs.eventb.AbstractState;
import graphs.eventb.ConcreteState;
import graphs.eventb.ConcreteTransition;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 19:52
 */
public final class EventSystemUnderApproximation implements IDOTFormatterVisitable {

    private final LinkedHashSet<ConcreteState> C;
    private final LinkedHashSet<ConcreteState> C0;
    private final LinkedHashMap<ConcreteState, AbstractState> Alpha;
    private final LinkedHashMap<ConcreteState, EStateColor> Kappa;
    private final LinkedHashSet<ConcreteTransition> DeltaC;

    public EventSystemUnderApproximation(LinkedHashSet<ConcreteState> C, LinkedHashSet<ConcreteState> C0, LinkedHashMap<ConcreteState, AbstractState> Alpha, LinkedHashMap<ConcreteState, EStateColor> Kappa, LinkedHashSet<ConcreteTransition> DeltaC) {
        int i = 0;
        for (ConcreteState concreteState : C) {
            concreteState.setName(concreteState.getName() + "_" + i);
            for (ConcreteTransition concreteTransition : DeltaC) {
                if (concreteTransition.getSource() != concreteState && concreteTransition.getSource().equals(concreteState)) {
                    concreteTransition.getSource().setName(concreteState.getName());
                }
                if (concreteTransition.getTarget() != concreteState && concreteTransition.getTarget().equals(concreteState)) {
                    concreteTransition.getTarget().setName(concreteState.getName());
                }
            }
            i++;
        }
        this.C = C;
        this.C0 = C0;
        this.Alpha = Alpha;
        this.Kappa = Kappa;
        this.DeltaC = DeltaC;
    }

    @Override
    public String accept(DOTFormatter visitor) {
        return visitor.visit(this);
    }

    public LinkedHashSet<ConcreteState> getC() {
        return C;
    }

    public LinkedHashSet<ConcreteState> getC0() {
        return C0;
    }

    public LinkedHashMap<ConcreteState, AbstractState> getAlpha() {
        return Alpha;
    }

    public LinkedHashMap<ConcreteState, EStateColor> getKappa() {
        return Kappa;
    }

    public LinkedHashSet<ConcreteTransition> getDeltaC() {
        return DeltaC;
    }

}
