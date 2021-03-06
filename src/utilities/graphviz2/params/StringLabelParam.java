package utilities.graphviz2.params;

import utilities.graphviz2.GVizFormatter;

/**
 * Created by gvoiron on 05/01/17.
 * Time : 15:55
 */
public final class StringLabelParam extends ALabelParam<String> {

    public StringLabelParam(String value) {
        super(value);
    }

    @Override
    public String accept(GVizFormatter visitor) {
        return visitor.visit(this);
    }

}
