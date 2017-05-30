package algorithms;

import algorithms.outputs.ComputerResult;
import utilities.Time;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 22:33
 */
public abstract class AComputer<T> {

    public final ComputerResult<T> compute() {
        preCompute();
        long start = Time.now();
        T computed = compute_();
        long end = Time.now();
        postCompute();
        return new ComputerResult<>(computed, new Time(end - start));
    }

    private void preCompute() {
    }

    protected void postCompute() {
    }

    protected abstract T compute_();

}
