package utilities.graphviz.visitors;

/**
 * Created by gvoiron on 21/12/16.
 * Time : 15:07
 */
public interface IGraphvizFormatterVisitable {

    String accept(GraphvizFormatter visitor);

}
