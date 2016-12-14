package eventb.exprs.bool;

import eventb.exprs.arith.IntVariable;
import eventb.exprs.arith.QuantifiedVariable;
import eventb.visitors.EventBFormatter;
import eventb.visitors.Primer;
import eventb.visitors.SMTLibFormatter;
import eventb.visitors.UnPrimer;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 12/12/16.
 * Time : 14:36
 */
public final class ForAll extends AQuantifier {

    public ForAll(ABoolExpr expression, QuantifiedVariable... quantifiedVariables) {
        super(expression, quantifiedVariables);
    }

    @Override
    public ABoolExpr accept(Primer visitor) {
        return visitor.visit(this);
    }

    @Override
    public ABoolExpr accept(UnPrimer visitor) {
        return visitor.visit(this);
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public String accept(SMTLibFormatter visitor) {
        return visitor.visit(this);
    }

    @Override
    public LinkedHashSet<IntVariable> getVariables() {
        return getExpression().getVariables().stream().filter(intVariable -> getQuantifiedVariables().stream().noneMatch(quantifiedVariable -> quantifiedVariable.getVariable().equals(intVariable))).collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
