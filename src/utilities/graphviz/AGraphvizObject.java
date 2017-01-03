package utilities.graphviz;

import utilities.graphviz.visitors.AGraphvizFormatter;
import utilities.graphviz.visitors.GraphvizFormatter;
import utilities.graphviz.visitors.IGraphvizFormatterVisitable;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 15:03
 */
public abstract class AGraphvizObject implements IGraphvizFormatterVisitable {

    @Override
    public final String toString() {
        return accept(new GraphvizFormatter());
    }

}
