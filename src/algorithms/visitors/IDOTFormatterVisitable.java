package algorithms.visitors;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:05
 */
public interface IDOTFormatterVisitable {

    String accept(DOTFormatter visitor);

}
