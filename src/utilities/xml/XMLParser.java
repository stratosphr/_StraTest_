package utilities.xml;

import org.xml.sax.ErrorHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 13:54
 */
public final class XMLParser {

    public static XMLDocument parse(File file) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return new XMLDocument(documentBuilder.parse(file));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static XMLDocument parse(File file, String rootName, File dtd, ErrorHandler errorHandler) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(errorHandler);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEW_LINE + "<!DOCTYPE " + rootName + " SYSTEM \"" + dtd + "\">" + NEW_LINE + new String(Files.readAllBytes(file.toPath())).replaceAll("[<][?!].*", "")).getBytes("UTF-8"));
            return new XMLDocument(documentBuilder.parse(byteArrayInputStream));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
