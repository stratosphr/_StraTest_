package eventb.exprs;

import java.util.List;

/**
 * Created by gvoiron on 10/12/16.
 * Time : 13:58
 */
public interface INaryOperation {

    List<? extends AExpr> getOperands();

}
