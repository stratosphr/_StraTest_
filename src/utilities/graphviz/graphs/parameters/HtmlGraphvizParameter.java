package utilities.graphviz.graphs.parameters;

import utilities.graphviz.visitors.AGraphvizFormatter;

/**
 * Created by gvoiron on 27/12/16.
 * Time : 22:09
 */
public final class HtmlGraphvizParameter extends ANonGlobalGraphvizParameter {

    private final String name;
    private final String htmlValue;

    public HtmlGraphvizParameter(String name, String htmlValue) {
        this.name = name;
        this.htmlValue = htmlValue;
    }

    @Override
    public String accept(AGraphvizFormatter visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public String getHtmlValue() {
        return htmlValue;
    }

}
