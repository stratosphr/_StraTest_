package algorithms.utilities;

import algorithms.IComputer;
import com.microsoft.z3.Status;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Invariant;
import eventb.exprs.bool.Predicate;
import eventb.graphs.AbstractState;
import solvers.z3.Z3;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TreeMap;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 22:32
 */
public final class AbstractStatesComputer implements IComputer<LinkedHashSet<AbstractState>> {

    private final Invariant invariant;
    private final LinkedHashSet<Predicate> abstractionPredicates;

    public AbstractStatesComputer(Invariant invariant, LinkedHashSet<Predicate> abstractionPredicates) {
        this.invariant = invariant;
        this.abstractionPredicates = abstractionPredicates;
    }

    @Override
    public LinkedHashSet<AbstractState> compute() {
        Z3 z3 = new Z3();
        LinkedHashSet<AbstractState> abstractStates = new LinkedHashSet<>();
        for (int i = 0; i < Math.pow(2, getAbstractionPredicates().size()); i++) {
            TreeMap<ABoolExpr, Boolean> mapping = new TreeMap<>();
            Iterator<Predicate> iterator = getAbstractionPredicates().iterator();
            String binaryString = String.format("%" + getAbstractionPredicates().size() + "s", Integer.toBinaryString(i)).replace(' ', '0');
            for (int j = 0; j < binaryString.length(); j++) {
                mapping.put(iterator.next(), binaryString.charAt(j) == '1');
            }
            //AbstractState abstractState = new AbstractState("q" + i, mapping);
            AbstractState abstractState = new AbstractState("q" + binaryString, mapping);
            z3.setCode(new And(getInvariant(), abstractState));
            if (z3.checkSAT() == Status.SATISFIABLE) {
                abstractStates.add(abstractState);
            }
        }
        return abstractStates;
    }

    public Invariant getInvariant() {
        return invariant;
    }

    public LinkedHashSet<Predicate> getAbstractionPredicates() {
        return abstractionPredicates;
    }

}
