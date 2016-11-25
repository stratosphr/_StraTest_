package utilities.xml;

import org.w3c.dom.Document;
import utilities.xml.visitors.IXMLFormatterVisitable;
import utilities.xml.visitors.XMLFormatter;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 14:20
 */
public class XMLDocument implements IXMLFormatterVisitable {

    private final XMLNode root;

    public XMLDocument(Document document) {
        document.getDocumentElement().normalize();
        this.root = new XMLNode(document.getDocumentElement());
    }

    @Override
    public String accept(XMLFormatter visitor) {
        return visitor.visit(this);
    }

    public XMLNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return getRoot().toString();
    }

}
