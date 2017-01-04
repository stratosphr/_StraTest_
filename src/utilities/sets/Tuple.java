package utilities.sets;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 17:04
 */
public final class Tuple<First, Second> {

    private final First first;
    private final Second second;

    public Tuple(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + getFirst() + ", " + getSecond() + ")";
    }

}
