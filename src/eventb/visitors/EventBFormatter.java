package eventb.visitors;

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

}
