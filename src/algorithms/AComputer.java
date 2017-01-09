package algorithms;

import algorithms.outputs.ComputerResult;
import utilities.Time;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 22:33
 */
public abstract class AComputer<T> {

    public final ComputerResult<T> compute() {
        long start = Time.now();
        T computed = compute_();
        long end = Time.now();
        return new ComputerResult<>(computed, new Time(end - start));
    }

    protected abstract T compute_();

}
