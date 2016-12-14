package algorithms.utilities;

import com.microsoft.z3.Status;
import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Not;
import eventb.exprs.bool.Predicate;
import graphs.eventb.AbstractState;
import solvers.z3.Z3;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 14:55
 */
public final class AbstractStatesComputer {

    public static List<AbstractState> computeAbstractStates(Machine machine, List<Predicate> abstractionPredicates) {
        List<AbstractState> abstractStates = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, abstractionPredicates.size()); i++) {
            LinkedHashSet<ABoolExpr> operands = new LinkedHashSet<>();
            String binaryString = String.format("%" + abstractionPredicates.size() + "s", Integer.toBinaryString(i)).replace(' ', '0');
            for (int j = 0; j < binaryString.length(); j++) {
                char c = binaryString.charAt(j);
                if (c == '0') {
                    operands.add(new Not(abstractionPredicates.get(j)));
                } else if (c == '1') {
                    operands.add(abstractionPredicates.get(j));
                } else {
                    throw new Error("The binary representation of a number should never contain any other characters than '0' and '1'.");
                }
            }
            And and = new And(operands.stream().toArray(ABoolExpr[]::new));
            Z3 z3 = new Z3();
            z3.setCode(new And(machine.getInvariant(), and));
            if (z3.checkSAT() == Status.SATISFIABLE) {
                abstractStates.add(new AbstractState("q" + i, operands));
            }
        }
        return abstractStates;
    }

}
