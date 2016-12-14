package algorithms.utilities;

import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import graphs.eventb.AbstractState;
import solvers.z3.Z3;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.z3.Status.SATISFIABLE;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 15:31
 */
public final class AbstractStateComputer {

    public static AbstractState computeAbstractState(ABoolExpr expression, Machine machine, List<AbstractState> abstractStates) {
        Z3 z3 = new Z3();
        List<AbstractState> abstractStatesList = new ArrayList<>(abstractStates);
        for (int i = 0; i < abstractStates.size(); i++) {
            z3.setCode(new And(expression, machine.getInvariant(), abstractStatesList.get(i)));
            if (z3.checkSAT() == SATISFIABLE) {
                return abstractStatesList.get(i);
            }
        }
        throw new Error("Impossible case encountered in abstract state computation: expression \"" + expression + "\" does not have any abstract state.");
    }

}
