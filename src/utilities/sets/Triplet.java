package utilities.sets;

/**
 * Created by gvoiron on 03/01/17.
 * Time : 15:09
 */
public final class Triplet<First, Second, Third> {

    private final First first;
    private final Second second;
    private final Third third;

    public Triplet(First first, Second second, Third third) {
        this.first = first;
        this.second = second;
        this.third = third;
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

    @Override
    public String toString() {
        return "(" + getFirst() + ", " + getSecond() + ", " + getThird() + ")";
    }

}
