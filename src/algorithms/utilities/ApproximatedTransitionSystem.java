package algorithms.utilities;

import algorithms.visitors.DOTFormatter;
import algorithms.visitors.IDOTFormatterVisitable;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 18:20
 */
public final class ApproximatedTransitionSystem implements IDOTFormatterVisitable {

    private final EventSystem eventSystem;
    private final TriModalTransitionSystem triModalTransitionSystem;
    private final EventSystemUnderApproximation eventSystemUnderApproximation;

    public ApproximatedTransitionSystem(EventSystem eventSystem, TriModalTransitionSystem triModalTransitionSystem, EventSystemUnderApproximation eventSystemUnderApproximation) {
        this.eventSystem = eventSystem;
        this.triModalTransitionSystem = triModalTransitionSystem;
        this.eventSystemUnderApproximation = eventSystemUnderApproximation;
    }

    @Override
    public String accept(DOTFormatter visitor) {
        return visitor.visit(this);
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }

    public TriModalTransitionSystem getTriModalTransitionSystem() {
        return triModalTransitionSystem;
    }

    public EventSystemUnderApproximation getEventSystemUnderApproximation() {
        return eventSystemUnderApproximation;
    }

}
