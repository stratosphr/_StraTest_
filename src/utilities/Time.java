package utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gvoiron on 09/01/17.
 * Time : 11:20
 */
public final class Time {

    private final long nanoSeconds;

    public Time(long nanoSeconds) {
        this.nanoSeconds = nanoSeconds;
    }

    public long getNanoSeconds() {
        return nanoSeconds;
    }

    public static long now() {
        return System.nanoTime();
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("mm:ss.SSS").format(new Date(getNanoSeconds() / 1000000));
    }

}
