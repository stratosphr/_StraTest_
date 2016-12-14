package graphs.dot;

import algorithms.visitors.DOTFormatter;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:12
 */
public final class DOTState extends ADOTState {

    public DOTState(String name) {
        this(name, name);
    }

    public DOTState(String name, String label) {
        super(name, label);
    }

    @Override
    public String accept(DOTFormatter visitor) {
        return visitor.visit(this);
    }

}
