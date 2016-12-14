package graphs.dot;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 20:21
 */
public abstract class ADOTState extends ADOTObject {

    private final String name;
    private final String label;

    public ADOTState(String name, String label){
        this.name = name;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

}
