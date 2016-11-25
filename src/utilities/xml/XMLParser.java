package utilities.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 13:54
 */
public final class XMLParser {

    public static XMLDocument parse(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return new XMLDocument(dBuilder.parse(file));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
