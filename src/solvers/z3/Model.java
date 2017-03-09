package solvers.z3;

import com.microsoft.z3.FuncDecl;
import eventb.exprs.arith.AAssignable;
import eventb.exprs.arith.Int;
import eventb.exprs.arith.IntVariable;
import eventb.visitors.Primer;

import java.util.LinkedHashSet;
import java.util.TreeMap;

/**
 * Created by gvoiron on 17/08/16.
 * Time : 10:37
 */
public final class Model {

    private final com.microsoft.z3.Model model;
    private final TreeMap<IntVariable, Int> source;
    private final TreeMap<IntVariable, Int> target;

    public Model(Z3 z3, com.microsoft.z3.Model model) {
        this.model = model;
        this.source = new TreeMap<>();
        this.target = new TreeMap<>();
        for (FuncDecl variable : getModel().getDecls()) {
            String stringValue = getModel().eval(z3.getContext().mkIntConst(variable.getName()), false).toString();
            try {
                Int value = new Int(Integer.parseInt(stringValue));
                String name = variable.getName().toString();
                if (name.endsWith(Primer.PRIME_SUFFIX)) {
                    if (!new IntVariable(name).unPrime().toString().endsWith(Primer.PRIME_SUFFIX)) {
                        target.put((IntVariable) new IntVariable(name).unPrime(), value);
                    }
                } else {
                    source.put(new IntVariable(name), value);
                }
            } catch (NumberFormatException ignored) {
                //throw new Error("The value \"" + stringValue + "\" returned by Z3 is not a valid integer.");
            }
        }
    }

    public Model(Z3 z3, com.microsoft.z3.Model model, LinkedHashSet<AAssignable> assignables) {
        this(z3, model);
        getSource().keySet().removeIf(intVariable -> !assignables.contains(intVariable));
        getTarget().keySet().removeIf(intVariable -> !assignables.contains(intVariable));
    }

    public TreeMap<IntVariable, Int> getSource() {
        return source;
    }

    public TreeMap<IntVariable, Int> getTarget() {
        return target;
    }

    private com.microsoft.z3.Model getModel() {
        return model;
    }

}
