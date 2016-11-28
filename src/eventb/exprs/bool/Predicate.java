package eventb.exprs.bool;

import eventb.visitors.EventBFormatter;

/**
 * Created by gvoiron on 27/11/16.
 * Time : 12:54
 */
public class Predicate extends ABoolExpr {

    private final String name;
    private final ABoolExpr expression;

    public Predicate(String name, ABoolExpr expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public ABoolExpr getExpression() {
        return expression;
    }

}
