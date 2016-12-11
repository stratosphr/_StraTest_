package utilities.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import utilities.xml.visitors.IXMLFormatterVisitable;
import utilities.xml.visitors.XMLFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 13:55
 */
public final class XMLNode implements IXMLFormatterVisitable {

    private final String name;
    private final Map<String, String> attributes;
    private final ArrayList<XMLNode> children;

    public XMLNode(Element element) {
        this.name = element.getNodeName();
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            attributes.put(element.getAttributes().item(i).getNodeName(), element.getAttributes().item(i).getNodeValue());
        }
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            if (element.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                children.add(new XMLNode((Element) element.getChildNodes().item(i)));
            }
        }
    }

    @Override
    public String accept(XMLFormatter visitor) {
        return visitor.visit(this);
    }

    public XMLNode getFirstChildWithName(String name) {
        return getChildren().stream().filter(xmlNode -> xmlNode.getName().equals(name)).findFirst().orElse(null);
    }

    public List<XMLNode> getChildrenWithName(String name) {
        return getChildren().stream().filter(xmlNode -> xmlNode.getName().equals(name)).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public ArrayList<XMLNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return accept(new XMLFormatter());
    }

}
