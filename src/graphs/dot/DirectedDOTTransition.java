package graphs.dot;

import algorithms.visitors.DOTFormatter;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:13
 */
public final class DirectedDOTTransition extends ADOTTransition {

    public DirectedDOTTransition(ADOTState source, ADOTState target) {
        this(source, "", target);
    }

    public DirectedDOTTransition(ADOTState source, String label, ADOTState target) {
        super(source, label, target);
    }

    @Override
    public String accept(DOTFormatter visitor) {
        return visitor.visit(this);
    }

}
