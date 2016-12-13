package eventb.exprs.arith;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 15:17
 */
public abstract class AAssignable extends AArithExpr implements Comparable<AAssignable> {

    private String name;

    public AAssignable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(AAssignable assignable) {
        return toString().compareTo(assignable.toString());
    }

}
