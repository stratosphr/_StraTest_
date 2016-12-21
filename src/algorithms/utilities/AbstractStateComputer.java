package algorithms.utilities;

import algorithms.IComputer;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Invariant;
import eventb.graphs.AbstractState;
import solvers.z3.Z3;

import java.util.LinkedHashSet;

import static com.microsoft.z3.Status.SATISFIABLE;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 22:32
 */
public final class AbstractStateComputer implements IComputer<AbstractState> {

    private final ABoolExpr expression;
    private final Invariant invariant;
    private final LinkedHashSet<AbstractState> abstractStates;

    public AbstractStateComputer(ABoolExpr expression, Invariant invariant, LinkedHashSet<AbstractState> abstractStates) {
        this.expression = expression;
        this.invariant = invariant;
        this.abstractStates = abstractStates;
    }

    @Override
    public AbstractState compute() {
        Z3 z3 = new Z3();
        for (AbstractState abstractState : getAbstractStates()) {
            z3.setCode(new And(getInvariant(), getExpression(), abstractState));
            if (z3.checkSAT() == SATISFIABLE) {
                return abstractState;
            }
        }
        throw new Error("Unexpected case encountered in abstract state computation: expression \"" + getExpression() + "\" does not belong to any abstract state.");
    }

    public ABoolExpr getExpression() {
        return expression;
    }

    public Invariant getInvariant() {
        return invariant;
    }

    public LinkedHashSet<AbstractState> getAbstractStates() {
        return abstractStates;
    }

}
