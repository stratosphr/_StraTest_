package eventb.exprs.arith;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:17
 */
public abstract class AAssignable extends AArithExpr {

    private String name;

    public AAssignable(String name) {
        this.name = name;
    }

    @Override
    public abstract AAssignable clone();

    public String getName() {
        return name;
    }

}
