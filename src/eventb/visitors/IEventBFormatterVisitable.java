package eventb.visitors;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:25
 */
public interface IEventBFormatterVisitable {

    String accept(EventBFormatter visitor);

}
