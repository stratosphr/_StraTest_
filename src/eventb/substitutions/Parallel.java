package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.visitors.EventBFormatter;
import utilities.Chars;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:34
 */
public final class Parallel extends ASubstitution {

    private final List<ASubstitution> substitutions;

    public Parallel(ASubstitution... substitutions) {
        if (substitutions.length == 0) {
            throw new Error("A \"Parallel\" substitution requires at least one substitution (none given).");
        }
        this.substitutions = Arrays.asList(substitutions);
    }

    @Override
    public ASubstitution clone() {
        return new Parallel(substitutions.stream().map(ASubstitution::clone).toArray(ASubstitution[]::new));
    }

    @Override
    public ABoolExpr getPrd(Machine machine) {
        return getSurrogate().getPrd(machine);
    }

    public ASubstitution getSurrogate() {
        ASubstitution substitution = getSubstitutions().get(0);
        while (substitution instanceof Parallel) {
            substitution = ((Parallel) substitution).getSurrogate();
        }
        substitution = getSubstitutions().subList(1, getSubstitutions().size()).stream().reduce(substitution, this::getSurrogate_);
        return substitution;
    }

    private ASubstitution getSurrogate_(ASubstitution leftSubstitution, ASubstitution rightSubstitution) {
        if (leftSubstitution instanceof Skip) {
            return rightSubstitution;
        } else if (leftSubstitution instanceof AAssignment) {
            if (rightSubstitution instanceof AAssignment) {
                return new MultipleAssignment((AAssignment) leftSubstitution, (AAssignment) rightSubstitution);
            } else {
                return new Parallel(rightSubstitution, leftSubstitution).getSurrogate();
            }
        } else if (leftSubstitution instanceof IfThenElse) {
            return new IfThenElse(((IfThenElse) leftSubstitution).getCondition(), new Parallel(((IfThenElse) leftSubstitution).getThenPart(), rightSubstitution).getSurrogate(), new Parallel(((IfThenElse) leftSubstitution).getElsePart(), rightSubstitution).getSurrogate());
        }
        throw new Error("Unable to compute_ parallel substitution's surrogate: unhandled substitution of type \"" + leftSubstitution.getClass().getName() + "\" encoutered." + Chars.NEW_LINE + "The problematic substitution was:" + Chars.NEW_LINE + leftSubstitution);
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public List<ASubstitution> getSubstitutions() {
        return substitutions;
    }

}
