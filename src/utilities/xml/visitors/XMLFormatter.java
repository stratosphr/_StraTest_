package utilities.xml.visitors;

import utilities.UAFormatter;
import utilities.UChars;
import utilities.xml.XMLDocument;
import utilities.xml.XMLNode;

import java.util.stream.Collectors;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 15:00
 */
public final class XMLFormatter extends UAFormatter {

    public String visit(XMLDocument xmlDocument) {
        return xmlDocument.getRoot().accept(this);
    }

    public String visit(XMLNode xmlNode) {
        String str = "";
        str += "<" + xmlNode.getName() + (xmlNode.getAttributes().isEmpty() ? "" : xmlNode.getAttributes().keySet().stream().map(name -> name + "=\"" + xmlNode.getAttributes().get(name) + "\"").collect(Collectors.joining(" ", " ", "")));
        if (xmlNode.getChildren().isEmpty()) {
            str += "/>";
        } else {
            str += ">" + UChars.NEW_LINE;
            indentRight();
            for (XMLNode child : xmlNode.getChildren()) {
                str += indent() + child.accept(this) + UChars.NEW_LINE;
            }
            indentLeft();
            str += indent() + "<" + xmlNode.getName() + "/>";
        }
        return str;
    }

}
