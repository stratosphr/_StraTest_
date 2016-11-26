package eventb.visitors;

import eventb.events.*;
import eventb.exprs.arith.*;
import eventb.exprs.bool.*;
import utilities.UAFormatter;
import utilities.UChars;

import java.util.stream.Collectors;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:32
 */
public final class EventBFormatter extends UAFormatter {

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

    public String visit(Not not) {
        return UChars.LNOT + "(" + not.getOperand().accept(this) + ")";
    }

    public String visit(And and) {
        return and.getOperands().isEmpty() ? "true" : "(" + and.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" " + UChars.LAND + " ")) + ")";
    }

    public String visit(Or or) {
        return or.getOperands().isEmpty() ? "false" : "(" + or.getOperands().stream().map(operand -> operand.accept(this)).collect(Collectors.joining(" " + UChars.LOR + " ")) + ")";
    }

    public String visit(Equals equals) {
        return "(" + equals.getLeft().accept(this) + " = " + equals.getRight().accept(this) + ")";
    }

    public String visit(LowerThan lowerThan) {
        return "(" + lowerThan.getLeft().accept(this) + " < " + lowerThan.getRight().accept(this) + ")";
    }

    public String visit(LowerOrEqual lowerOrEqual) {
        return "(" + lowerOrEqual.getLeft().accept(this) + " " + UChars.LEQ + " " + lowerOrEqual.getRight().accept(this) + ")";
    }

    public String visit(GreaterThan greaterThan) {
        return "(" + greaterThan.getLeft().accept(this) + " > " + greaterThan.getRight().accept(this) + ")";
    }

    public String visit(GreaterOrEqual greaterOrEqual) {
        return "(" + greaterOrEqual.getLeft().accept(this) + " " + UChars.GEQ + " " + greaterOrEqual.getRight().accept(this) + ")";
    }

    public String visit(Event event) {
        String str = "";
        str += event.getName() + " " + UChars.EQ_DEF + UChars.NEW_LINE;
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
        str += "SELECT" + UChars.NEW_LINE;
        indentRight();
        str += indent() + select.getCondition().accept(this) + UChars.NEW_LINE;
        indentLeft();
        str += indent() + "THEN" + UChars.NEW_LINE;
        indentRight();
        str += indent() + select.getSubstitution().accept(this) + UChars.NEW_LINE;
        indentLeft();
        str += indent() + "END";
        return str;
    }

    public String visit(Choice choice) {
        String str = "";
        str += "CHOICE" + UChars.NEW_LINE;
        for (ASubstitution substitution : choice.getSubstitutions().subList(0, choice.getSubstitutions().size() - 1)) {
            indentRight();
            str += indent() + substitution.accept(this) + UChars.NEW_LINE;
            indentLeft();
            str += indent() + "OR" + UChars.NEW_LINE;
        }
        indentRight();
        str += indent() + choice.getSubstitutions().get(choice.getSubstitutions().size() - 1).accept(this) + UChars.NEW_LINE;
        indentLeft();
        str += indent() + "END";
        return str;
    }

    public String visit(Any any) {
        String str = "";
        str += "ANY" + UChars.NEW_LINE;
        indentRight();
        str += indent() + any.getQuantifiedVariables().stream().map(quantifiedVariable -> quantifiedVariable.accept(this)).collect(Collectors.joining(", ")) + UChars.NEW_LINE;
        indentLeft();
        str += indent() + "WHERE" + UChars.NEW_LINE;
        indentRight();
        str += indent() + any.getCondition().accept(this) + UChars.NEW_LINE;
        indentLeft();
        str += indent() + "THEN" + UChars.NEW_LINE;
        indentRight();
        str += indent() + any.getSubstitution().accept(this) + UChars.NEW_LINE;
        indentLeft();
        str += indent() + "END";
        return str;
    }

    public String visit(Parallel parallel) {
        return parallel.getSubstitutions().stream().map(substitution -> substitution.accept(this)).collect(Collectors.joining(" ||" + UChars.NEW_LINE + indent()));
    }

}
