package eventb.visitors;

import eventb.exprs.arith.*;
import eventb.exprs.bool.*;
import graphs.eventb.AbstractState;
import graphs.eventb.ConcreteState;

import java.util.LinkedHashSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:45
 */
public final class UnPrimer {

    private LinkedHashSet<QuantifiedVariable> quantifiedVariables;

    public UnPrimer() {
        this.quantifiedVariables = new LinkedHashSet<>();
    }

    public AArithExpr visit(Int anInt) {
        return anInt;
    }

    public AArithExpr visit(IntVariable intVariable) {
        return intVariable.getName().endsWith(Primer.PRIME_SUFFIX) ? new IntVariable(intVariable.getName().substring(0, intVariable.getName().length() - Primer.PRIME_SUFFIX.length())) : intVariable;
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
        return new Invariant(invariant.getExpression().accept(this));
    }

    public ABoolExpr visit(Not not) {
        return new Not(not.getOperand().accept(this));
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
        Exists unprimed = new Exists(exists.getExpression().accept(this), exists.getQuantifiedVariables().toArray(new QuantifiedVariable[exists.getQuantifiedVariables().size()]));
        quantifiedVariables = new LinkedHashSet<>(oldQuantifiedVariables);
        return unprimed;
    }

    public ABoolExpr visit(ForAll forAll) {
        LinkedHashSet<QuantifiedVariable> oldQuantifiedVariables = quantifiedVariables;
        quantifiedVariables = new LinkedHashSet<>();
        quantifiedVariables.addAll(oldQuantifiedVariables);
        quantifiedVariables.addAll(forAll.getQuantifiedVariables());
        ForAll unprimed = new ForAll(forAll.getExpression().accept(this), forAll.getQuantifiedVariables().toArray(new QuantifiedVariable[forAll.getQuantifiedVariables().size()]));
        quantifiedVariables = new LinkedHashSet<>(oldQuantifiedVariables);
        return unprimed;
    }

}
