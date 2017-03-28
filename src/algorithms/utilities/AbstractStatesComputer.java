package algorithms.utilities;

import algorithms.AComputer;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Invariant;
import eventb.exprs.bool.Predicate;
import eventb.graphs.AbstractState;
import solvers.z3.Z3;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TreeMap;

import static com.microsoft.z3.Status.SATISFIABLE;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 22:32
 */
public final class AbstractStatesComputer extends AComputer<LinkedHashSet<AbstractState>> {

    private final Invariant invariant;
    private final LinkedHashSet<Predicate> abstractionPredicates;
    private final Z3 z3;

    public AbstractStatesComputer(Invariant invariant, LinkedHashSet<Predicate> abstractionPredicates) {
        this.invariant = invariant;
        this.abstractionPredicates = abstractionPredicates;
        this.z3 = new Z3();
    }

    @Override
    protected LinkedHashSet<AbstractState> compute_() {
        LinkedHashSet<AbstractState> abstractStates = new LinkedHashSet<>();
        for (int i = 0; i < Math.pow(2, getAbstractionPredicates().size()); i++) {
            TreeMap<ABoolExpr, Boolean> mapping = new TreeMap<>();
            Iterator<Predicate> iterator = getAbstractionPredicates().iterator();
            String binaryString = String.format("%" + getAbstractionPredicates().size() + "s", Integer.toBinaryString(i)).replace(' ', '0');
            for (int j = 0; j < binaryString.length(); j++) {
                mapping.put(iterator.next(), binaryString.charAt(j) == '1');
            }
            AbstractState abstractState = new AbstractState("q" + i, mapping);
            //AbstractState abstractState = new AbstractState("q" + binaryString, mapping);
            z3.setCode(new And(getInvariant(), abstractState));
            if (z3.checkSAT() == SATISFIABLE) {
                abstractStates.add(abstractState);
            }
            //System.out.print("ASC: " + 100.0 * i / Math.pow(2, getAbstractionPredicates().size()) + " | ");
        }
        //System.out.println();
        return abstractStates;
    }

    public Invariant getInvariant() {
        return invariant;
    }

    public LinkedHashSet<Predicate> getAbstractionPredicates() {
        return abstractionPredicates;
    }

}
