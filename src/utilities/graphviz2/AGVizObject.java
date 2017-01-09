package utilities.graphviz2;

/**
 * Created by gvoiron on 05/01/17.
 * Time : 15:48
 */
public abstract class AGVizObject implements IGVizFormatterVisitable {

    @Override
    public final String toString() {
        return accept(new GVizFormatter());
    }

}
