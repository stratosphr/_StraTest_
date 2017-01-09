package utilities.graphviz2;

import utilities.AFormatter;
import utilities.graphviz2.params.ANonGlobalParam;
import utilities.graphviz2.params.HTML;
import utilities.graphviz2.params.HTMLLabelParam;
import utilities.graphviz2.params.StringLabelParam;

/**
 * Created by gvoiron on 05/01/17.
 * Time : 16:04
 */
public final class GVizFormatter extends AFormatter {

    private String formatParam(ANonGlobalParam parameter, String value) {
        return parameter.getName() + "=" + value;
    }

    public String visit(HTML html) {
        return html.getHtml();
    }

    public String visit(StringLabelParam stringLabelParam) {
        return formatParam(stringLabelParam, "\"" + stringLabelParam.getValue() + "\"");
    }

    public String accept(HTMLLabelParam htmlLabelParam) {
        return formatParam(htmlLabelParam, htmlLabelParam.getValue().getHtml());
    }

}
