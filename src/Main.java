import utilities.xml.XMLDocument;
import utilities.xml.XMLNode;
import utilities.xml.XMLParser;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        //XMLDocument parse = XMLParser.parse(new File("resources/eventb/threeBatteries/threeBatteries.ebm"));
        XMLDocument document = XMLParser.parse(new File("resources/eventb/simple/simple.ebm"));
        XMLNode root = document.getRoot();
        System.out.println(root.getChildrenWithName("INITIALISATION"));
        System.out.println(root.getChildrenWithName("VARIABLES"));
        System.out.println(root.getFirstChildWithName("INITIALISATION"));
        System.out.println(root.getFirstChildWithName("VARIABLES"));
    }

}
