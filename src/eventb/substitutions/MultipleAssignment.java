package eventb.substitutions;

import eventb.Machine;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Equals;
import eventb.visitors.EventBFormatter;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by gvoiron on 12/12/16.
 * Time : 16:55
 */
public final class MultipleAssignment extends AAssignment {

    private final LinkedHashSet<ASingleAssignment> assignments;

    public MultipleAssignment(AAssignment... assignments) {
        if (assignments.length < 2) {
            throw new Error("A multiple assignment substitution requires at least 2 assignments (" + assignments.length + " given).");
        }
        this.assignments = Stream.of(assignments).flatMap(assignment -> assignment instanceof MultipleAssignment ? ((MultipleAssignment) assignment).getAssignments().stream() : Stream.of((ASingleAssignment) assignment)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public MultipleAssignment clone() {
        return new MultipleAssignment(assignments.stream().map(ASingleAssignment::clone).toArray(AAssignment[]::new));
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public ABoolExpr getPrd(Machine machine) {
        return new And(Stream.concat(getAssignments().stream().map(singleAssignment -> singleAssignment.getPrd_(machine, true)), machine.getAssignables().stream().filter(assignable -> getAssignments().stream().noneMatch(singleAssignment -> singleAssignment.getAssignable().equals(assignable))).map(assignable -> new Equals(assignable.prime(), assignable))).toArray(ABoolExpr[]::new));
    }

    public LinkedHashSet<ASingleAssignment> getAssignments() {
        return assignments;
    }

}
