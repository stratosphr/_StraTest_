package utilities.sets;

/**
 * Created by gvoiron on 03/01/17.
 * Time : 15:09
 */
public final class Quadruplet<First, Second, Third, Fourth> {

    private final First first;
    private final Second second;
    private final Third third;
    private final Fourth fourth;

    public Quadruplet(First first, Second second, Third third, Fourth fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    public Third getThird() {
        return third;
    }

    public Fourth getFourth() {
        return fourth;
    }

    @Override
    public String toString() {
        return "(" + getFirst() + ", " + getSecond() + ", " + getThird() + ", " + getFourth() + ")";
    }

}
