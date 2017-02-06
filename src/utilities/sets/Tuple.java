package utilities.sets;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 17:04
 */
public final class Tuple<First, Second> {

    private First first;
    private Second second;

    public Tuple(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public Second getSecond() {
        return second;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + getFirst() + ", " + getSecond() + ")";
    }

}
