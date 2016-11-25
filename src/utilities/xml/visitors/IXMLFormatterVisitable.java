package utilities.xml.visitors;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 14:58
 */
public interface IXMLFormatterVisitable {

    String accept(XMLFormatter visitor);

}
