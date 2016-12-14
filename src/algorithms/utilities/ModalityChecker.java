package algorithms.utilities;

import com.microsoft.z3.Status;
import eventb.Machine;
import eventb.exprs.bool.And;
import graphs.eventb.AbstractTransition;
import solvers.z3.Model;
import solvers.z3.Z3;
import utilities.Tuple;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 17:31
 */
public final class ModalityChecker {

    public static Tuple<Boolean, Model> isMayWithModel(AbstractTransition abstractTransition, Machine machine) {
        Z3 z3 = new Z3();
        z3.setCode(new And(
                machine.getInvariant(),
                machine.getInvariant().prime(),
                abstractTransition.getSource().getExpression(),
                abstractTransition.getLabel().getSubstitution().getPrd(machine),
                abstractTransition.getTarget().getExpression().prime()
        ));
        return z3.checkSAT() == Status.SATISFIABLE ? new Tuple<>(true, z3.getModel()) : new Tuple<>(false, null);
    }

}
