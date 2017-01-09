package utilities.graphviz2.params;

/**
 * Created by gvoiron on 05/01/17.
 * Time : 15:49
 */
public abstract class ANonGlobalParam<Type> extends AParam {

    private final String name;
    private final Type value;

    public ANonGlobalParam(String name, Type value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Type getValue() {
        return value;
    }

}
