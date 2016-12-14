package graphs.dot;

import algorithms.visitors.DOTFormatter;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:10
 */
public final class DirectedDOTGraph extends ADOTGraph {

    public DirectedDOTGraph(LinkedHashSet<ADOTState> states, LinkedHashSet<DirectedDOTTransition> transitions) {
        super(states, transitions);
    }

    @Override
    public String accept(DOTFormatter visitor) {
        return visitor.visit(this);
    }

}
