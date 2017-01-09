package algorithms.outputs;

import utilities.Time;

/**
 * Created by gvoiron on 09/01/17.
 * Time : 13:38
 */
public final class ComputerResult<T> {

    private final T computed;
    private final Time time;

    public ComputerResult(T computed, Time time) {
        this.computed = computed;
        this.time = time;
    }

    public T getResult() {
        return computed;
    }

    public Time getComputationTime() {
        return time;
    }

}
