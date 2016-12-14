package eventb.exprs.bool;

import eventb.exprs.arith.IntVariable;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:09
 */
public abstract class APredicate extends ABoolExpr {

    private String name;
    private final ABoolExpr expression;

    public APredicate(String name, ABoolExpr expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public LinkedHashSet<IntVariable> getVariables() {
        return getExpression().getVariables();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ABoolExpr getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return getExpression().hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return getClass().equals(o.getClass()) && ((this == o) || getExpression().equals(((APredicate) o).getExpression()));
    }

}
