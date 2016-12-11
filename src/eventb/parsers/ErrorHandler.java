package eventb.parsers;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Created by gvoiron on 29/11/16.
 * Time : 14:09
 */
public final class ErrorHandler implements org.xml.sax.ErrorHandler {

    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.err.println("l." + e.getLineNumber() + ", c." + e.getColumnNumber() + ": " + e.getMessage());
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        System.err.println("l." + e.getLineNumber() + ", c." + e.getColumnNumber() + ": " + e.getMessage());
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        System.err.println("l." + e.getLineNumber() + ", c." + e.getColumnNumber() + ": " + e.getMessage());
    }

}
