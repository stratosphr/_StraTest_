package solvers.z3;

import com.microsoft.z3.FuncDecl;
import eventb.exprs.arith.Int;
import eventb.exprs.arith.IntVariable;

import java.util.TreeMap;

/**
 * Created by gvoiron on 17/08/16.
 * Time : 10:37
 */
public final class Model extends TreeMap<IntVariable, Int> {

    private final com.microsoft.z3.Model model;

    public Model(Z3 z3, com.microsoft.z3.Model model) {
        this.model = model;
        for (FuncDecl variable : getModel().getDecls()) {
            String stringValue = getModel().eval(z3.getContext().mkIntConst(variable.getName()), false).toString();
            try {
                Int value = new Int(Integer.parseInt(stringValue));
                put(new IntVariable(variable.getName().toString()), value);
            } catch (NumberFormatException ignored) {
                throw new Error("The value \"" + stringValue + "\" returned by Z3 is not a valid integer.");
            }
        }
    }

    private com.microsoft.z3.Model getModel() {
        return model;
    }

}
