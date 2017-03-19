package eventb.parsers;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;

/**
 * Created by gvoiron on 29/11/16.
 * Time : 14:09
 */
public final class ErrorHandler implements org.xml.sax.ErrorHandler {

    private final File xmlFile;

    public ErrorHandler(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.err.println(xmlFile);
        System.err.println("l." + e.getLineNumber() + ", c." + e.getColumnNumber() + ": " + e.getMessage());
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        System.err.println(xmlFile);
        System.err.println("l." + e.getLineNumber() + ", c." + e.getColumnNumber() + ": " + e.getMessage());
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        System.err.println(xmlFile);
        System.err.println("l." + e.getLineNumber() + ", c." + e.getColumnNumber() + ": " + e.getMessage());
    }

}
