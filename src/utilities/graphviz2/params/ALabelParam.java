package utilities.graphviz2.params;

/**
 * Created by gvoiron on 05/01/17.
 * Time : 15:51
 */
public abstract class ALabelParam<Type> extends ANonGlobalParam<Type> implements IEdgeParam, IGraphParam, INodeParam, IClusterParam {

    public ALabelParam(Type value){
        super("label", value);
    }

}
