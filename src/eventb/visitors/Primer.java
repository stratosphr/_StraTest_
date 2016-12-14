package eventb.visitors;

import eventb.exprs.arith.*;
import eventb.exprs.bool.*;
import graphs.eventb.AbstractState;
import graphs.eventb.ConcreteState;

import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 12/12/16.
 * Time : 14:02
 */
public final class Primer {

    public static final String PRIME_SUFFIX = "_";

    private LinkedHashSet<QuantifiedVariable> quantifiedVariables;

    public Primer() {
        this.quantifiedVariables = new LinkedHashSet<>();
    }

    private static String prime(String name) {
        return name + PRIME_SUFFIX;
    }

    public AArithExpr visit(Int anInt) {
        return anInt;
    }

    public AArithExpr visit(IntVariable intVariable) {
        if (quantifiedVariables.stream().anyMatch(quantifiedVariable -> quantifiedVariable.getVariable().equals(intVariable))) {
            return new QuantifiedVariable(intVariable).accept(this);
        } else {
            return new IntVariable(prime(intVariable.getName()));
        }
    }

    public AArithExpr visit(QuantifiedVariable quantifiedVariable) {
        return quantifiedVariable;
    }

    public AArithExpr visit(Sum sum) {
        return new Sum(sum.getOperands().stream().map(operand -> operand.accept(this)).toArray(AArithExpr[]::new));
    }

    public AArithExpr visit(Subtraction subtraction) {
        return new Subtraction(subtraction.getOperands().stream().map(operand -> operand.accept(this)).toArray(AArithExpr[]::new));
    }

    public AArithExpr visit(Product product) {
        return new Product(product.getOperands().stream().map(operand -> operand.accept(this)).toArray(AArithExpr[]::new));
    }

    public AArithExpr visit(Division division) {
        return new Division(division.getOperands().stream().map(operand -> operand.accept(this)).toArray(AArithExpr[]::new));
    }

    public ABoolExpr visit(False aFalse) {
        return aFalse;
    }

    public ABoolExpr visit(True aTrue) {
        return aTrue;
    }

    public ABoolExpr visit(Predicate predicate) {
        return new Predicate(predicate.getName(), predicate.getExpression().accept(this));
    }

    public ABoolExpr visit(AbstractState abstractState) {
        return new AbstractState(abstractState.getName(), abstractState.getPredicates().stream().map(predicate -> predicate.accept(this)).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public ABoolExpr visit(ConcreteState concreteState) {
        TreeMap<IntVariable, Int> state = new TreeMap<>();
        for (IntVariable intVariable : concreteState.getState().keySet()) {
            state.put((IntVariable) intVariable.accept(this), (Int) concreteState.getState().get(intVariable).accept(this));
        }
        return new ConcreteState(concreteState.getName(), state);
    }

    public ABoolExpr visit(Invariant invariant) {
        return new Invariant(invariant.getExpression().prime());
    }

    public ABoolExpr visit(Not not) {
        return new Not(not.getOperand().prime());
    }

    public ABoolExpr visit(And and) {
        return new And(and.getOperands().stream().map(operand -> operand.accept(this)).toArray(ABoolExpr[]::new));
    }

    public ABoolExpr visit(Or or) {
        return new Or(or.getOperands().stream().map(operand -> operand.accept(this)).toArray(ABoolExpr[]::new));
    }

    public ABoolExpr visit(Equals equals) {
        return new Equals(equals.getLeft().accept(this), equals.getRight().accept(this));
    }

    public ABoolExpr visit(LowerThan lowerThan) {
        return new LowerThan(lowerThan.getLeft().accept(this), lowerThan.getRight().accept(this));
    }

    public ABoolExpr visit(LowerOrEqual lowerOrEqual) {
        return new LowerOrEqual(lowerOrEqual.getLeft().accept(this), lowerOrEqual.getRight().accept(this));
    }

    public ABoolExpr visit(GreaterThan greaterThan) {
        return new GreaterThan(greaterThan.getLeft().accept(this), greaterThan.getRight().accept(this));
    }

    public ABoolExpr visit(GreaterOrEqual greaterOrEqual) {
        return new GreaterOrEqual(greaterOrEqual.getLeft().accept(this), greaterOrEqual.getRight().accept(this));
    }

    public ABoolExpr visit(Exists exists) {
        LinkedHashSet<QuantifiedVariable> oldQuantifiedVariables = quantifiedVariables;
        quantifiedVariables = new LinkedHashSet<>();
        quantifiedVariables.addAll(oldQuantifiedVariables);
        quantifiedVariables.addAll(exists.getQuantifiedVariables());
        Exists primed = new Exists(exists.getExpression().accept(this), exists.getQuantifiedVariables().stream().map(quantifiedVariable -> quantifiedVariable.accept(this)).toArray(QuantifiedVariable[]::new));
        quantifiedVariables = oldQuantifiedVariables;
        return primed;
    }

    public ABoolExpr visit(ForAll forAll) {
        LinkedHashSet<QuantifiedVariable> oldQuantifiedVariables = quantifiedVariables;
        quantifiedVariables = new LinkedHashSet<>();
        quantifiedVariables.addAll(oldQuantifiedVariables);
        quantifiedVariables.addAll(forAll.getQuantifiedVariables());
        ForAll primed = new ForAll(forAll.getExpression().accept(this), forAll.getQuantifiedVariables().stream().map(quantifiedVariable -> quantifiedVariable.accept(this)).toArray(QuantifiedVariable[]::new));
        quantifiedVariables = oldQuantifiedVariables;
        return primed;
    }

}
