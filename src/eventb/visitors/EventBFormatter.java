package eventb.visitors;

import eventb.Event;
import eventb.Machine;
import eventb.substitutions.*;
import eventb.exprs.arith.*;
import eventb.exprs.bool.*;
import utilities.AFormatter;

import java.util.stream.Collectors;

import static utilities.Chars.*;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:32
 */
public final class EventBFormatter extends AFormatter {

    public String visit(Int anInt) {
        return String.valueOf(anInt.getValue());
    }

    public String visit(IntVariable intVariable) {
        return intVariable.getName();
    }

    public String visit(Sum sum) {
        return "(" + sum.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" + ")) + ")";
    }

    public String visit(Subtraction subtraction) {
        return "(" + subtraction.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" - ")) + ")";
    }

    public String visit(Product product) {
        return "(" + product.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" * ")) + ")";
    }

    public String visit(Division division) {
        return "(" + division.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" / ")) + ")";
    }

    public String visit(False aFalse) {
        return "false";
    }

    public String visit(True aTrue) {
        return "true";
    }

    public String visit(Predicate predicate) {
        return "(" + predicate.getName() + " " + EQ_DEF + " " + predicate.getExpression().accept(this) + ")";
    }

    public String visit(Not not) {
        return LNOT + "(" + not.getOperand().accept(this) + ")";
    }

    public String visit(And and) {
        return and.getOperands().isEmpty() ? "true" : "(" + and.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" " + LAND + " ")) + ")";
    }

    public String visit(Or or) {
        return or.getOperands().isEmpty() ? "false" : "(" + or.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" " + LOR + " ")) + ")";
    }

    public String visit(Equals equals) {
        return "(" + equals.getLeft().accept(this) + " = " + equals.getRight().accept(this) + ")";
    }

    public String visit(LowerThan lowerThan) {
        return "(" + lowerThan.getLeft().accept(this) + " < " + lowerThan.getRight().accept(this) + ")";
    }

    public String visit(LowerOrEqual lowerOrEqual) {
        return "(" + lowerOrEqual.getLeft().accept(this) + " " + LEQ + " " + lowerOrEqual.getRight().accept(this) + ")";
    }

    public String visit(GreaterThan greaterThan) {
        return "(" + greaterThan.getLeft().accept(this) + " > " + greaterThan.getRight().accept(this) + ")";
    }

    public String visit(GreaterOrEqual greaterOrEqual) {
        return "(" + greaterOrEqual.getLeft().accept(this) + " " + GEQ + " " + greaterOrEqual.getRight().accept(this) + ")";
    }

    public String visit(Invariant invariant) {
        return invariant.getExpression().accept(this);
    }

    public String visit(Machine machine) {
        String str = "";
        str += "MACHINE" + NEW_LINE;
        indentRight();
        str += indent() + machine.getName() + NEW_LINE;
        indentLeft();
        if (!machine.getSets().isEmpty()) {
            throw new Error("Sets are not yet handled by the formatter.");
        }
        if (!machine.getAssignables().isEmpty()) {
            str += NEW_LINE;
            str += indent() + "VARIABLES" + NEW_LINE;
            indentRight();
            str += indent() + machine.getAssignables().stream().map(assignable -> assignable.accept(this)).collect(Collectors.joining("," + NEW_LINE + indent())) + NEW_LINE;
            indentLeft();
        }
        str += NEW_LINE;
        str += indent() + "INVARIANT" + NEW_LINE;
        indentRight();
        str += indent() + machine.getInvariant().accept(this) + NEW_LINE;
        indentLeft();
        str += NEW_LINE;
        str += indent() + "INITIALISATION" + NEW_LINE;
        indentRight();
        str += indent() + machine.getInitialisation().accept(this) + NEW_LINE;
        indentLeft();
        str += NEW_LINE;
        str += indent() + "EVENTS" + NEW_LINE;
        indentRight();
        str += indent() + machine.getEvents().stream().map(event -> event.accept(this)).collect(Collectors.joining(NEW_LINE + NEW_LINE + indent())) + NEW_LINE;
        indentLeft();
        return str;
    }

    public String visit(Event event) {
        String str = "";
        str += event.getName() + " " + EQ_DEF + NEW_LINE;
        indentRight();
        str += indent() + event.getSubstitution().accept(this);
        indentLeft();
        return str;
    }

    public String visit(Skip skip) {
        return "SKIP";
    }

    public String visit(Assignment assignment) {
        return assignment.getAssignable().accept(this) + " := " + assignment.getValue().accept(this);
    }

    public String visit(Select select) {
        String str = "";
        str += "SELECT" + NEW_LINE;
        indentRight();
        str += indent() + select.getCondition().accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "THEN" + NEW_LINE;
        indentRight();
        str += indent() + select.getSubstitution().accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "END";
        return str;
    }

    public String visit(IfThenElse ifThenElse) {
        String str = "";
        str += "IF" + NEW_LINE;
        indentRight();
        str += indent() + ifThenElse.getCondition().accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "THEN" + NEW_LINE;
        indentRight();
        str += indent() + ifThenElse.getThenPart().accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "ELSE" + NEW_LINE;
        indentRight();
        str += indent() + ifThenElse.getElsePart().accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "END";
        return str;
    }

    public String visit(Choice choice) {
        String str = "";
        str += "CHOICE" + NEW_LINE;
        for (ASubstitution substitution : choice.getSubstitutions().subList(0, choice.getSubstitutions().size() - 1)) {
            indentRight();
            str += indent() + substitution.accept(this) + NEW_LINE;
            indentLeft();
            str += indent() + "OR" + NEW_LINE;
        }
        indentRight();
        str += indent() + choice.getSubstitutions().get(choice.getSubstitutions().size() - 1).accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "END";
        return str;
    }

    public String visit(Any any) {
        String str = "";
        str += "ANY" + NEW_LINE;
        indentRight();
        str += indent() + any.getQuantifiedVariables().stream().map(quantifiedVariable -> quantifiedVariable.accept(this)).collect(Collectors.joining(", ")) + NEW_LINE;
        indentLeft();
        str += indent() + "WHERE" + NEW_LINE;
        indentRight();
        str += indent() + any.getCondition().accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "THEN" + NEW_LINE;
        indentRight();
        str += indent() + any.getSubstitution().accept(this) + NEW_LINE;
        indentLeft();
        str += indent() + "END";
        return str;
    }

    public String visit(Parallel parallel) {
        return parallel.getSubstitutions().stream().map(substitution -> substitution.accept(this)).collect(Collectors.joining(" ||" + NEW_LINE + indent()));
    }

}
