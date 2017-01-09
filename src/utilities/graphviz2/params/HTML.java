package utilities.graphviz2.params;

import utilities.graphviz2.AGVizObject;
import utilities.graphviz2.GVizFormatter;

/**
 * Created by gvoiron on 05/01/17.
 * Time : 15:59
 */
public final class HTML extends AGVizObject {

    private final String html;

    public HTML(String html) {
        this.html = html;
    }

    @Override
    public String accept(GVizFormatter visitor) {
        return visitor.visit(this);
    }

    public String getHtml() {
        return html;
    }

}
