package graphs.dot;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:13
 */
public abstract class ADOTTransition extends ADOTObject {

    private final ADOTState source;
    private final String label;
    private final ADOTState target;

    public ADOTTransition(ADOTState source, ADOTState target) {
        this(source, "", target);
    }

    public ADOTTransition(ADOTState source, String label, ADOTState target) {
        this.source = source;
        this.label = label;
        this.target = target;
    }

    public ADOTState getSource() {
        return source;
    }

    public String getLabel() {
        return label;
    }

    public ADOTState getTarget() {
        return target;
    }

}
