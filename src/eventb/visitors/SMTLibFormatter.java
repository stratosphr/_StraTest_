package eventb.visitors;

import eventb.exprs.IBinaryOperation;
import eventb.exprs.INaryOperation;
import eventb.exprs.IUnaryOperation;
import eventb.exprs.arith.*;
import eventb.exprs.bool.*;
import utilities.AFormatter;

import java.util.stream.Collectors;

import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 10/12/16.
 * Time : 13:12
 */
public final class SMTLibFormatter extends AFormatter {

    public static String format(ABoolExpr expression) {
        return expression.getVariables().stream().map(variable -> "(declare-fun " + variable.getName() + " () Int)").collect(Collectors.joining(NEW_LINE)) + NEW_LINE + NEW_LINE + "(assert " + expression.accept(new SMTLibFormatter()) + ")";
    }

    private String visit(IUnaryOperation operation, String operator) {
        return "(" + operator + " " + operation.getOperand().accept(this) + ")";
    }

    private String visit(IBinaryOperation operation, String operator) {
        String formatted = "(" + operator + NEW_LINE;
        indentRight();
        formatted += indent() + operation.getLeft().accept(this) + NEW_LINE;
        formatted += indent() + operation.getRight().accept(this) + NEW_LINE;
        indentLeft();
        formatted += indent() + ")";
        return formatted;
    }

    private String visit(INaryOperation operation, String operator) {
        if (operation.getOperands().isEmpty()) {
            return operator;
        } else {
            String formatted = "(" + operator + NEW_LINE;
            indentRight();
            formatted += operation.getOperands().stream().map(operand -> indent() + operand.accept(this)).collect(Collectors.joining(NEW_LINE)) + NEW_LINE;
            indentLeft();
            formatted += indent() + ")";
            return formatted;
        }
    }

    public String visit(And and) {
        return visit(and, "and");
    }

    public String visit(Int anInt) {
        return anInt.getValue().toString();
    }

    public String visit(IntVariable intVariable) {
        return intVariable.getName();
    }

    public String visit(Sum sum) {
        return visit(sum, "+");
    }

    public String visit(Subtraction subtraction) {
        return visit(subtraction, "-");
    }

    public String visit(Product product) {
        return visit(product, "*");
    }

    public String visit(Division division) {
        return visit(division, "/");
    }

    public String visit(False aFalse) {
        return "false";
    }

    public String visit(True aTrue) {
        return "true";
    }

    public String visit(Predicate predicate) {
        return predicate.getExpression().accept(this);
    }

    public String visit(Not not) {
        return visit(not, "not");
    }

    public String visit(Or or) {
        return visit(or, "or");
    }

    public String visit(Equals equals) {
        return visit(equals, "=");
    }

    public String visit(LowerThan lowerThan) {
        return visit(lowerThan, "<");
    }

    public String visit(LowerOrEqual lowerOrEqual) {
        return visit(lowerOrEqual, "<=");
    }

    public String visit(GreaterThan greaterThan) {
        return visit(greaterThan, ">");
    }

    public String visit(GreaterOrEqual greaterOrEqual) {
        return visit(greaterOrEqual, ">=");
    }

    public String visit(Invariant invariant) {
        return invariant.getExpression().accept(this);
    }

}
