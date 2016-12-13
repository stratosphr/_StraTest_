package solvers.z3;

import com.microsoft.z3.*;
import eventb.exprs.bool.ABoolExpr;
import eventb.visitors.SMTLibFormatter;
import utilities.Chars;
import utilities.LibraryLinker;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utilities.Chars.NEW_LINE;

/**
 * Created by gvoiron on 10/12/16.
 * Time : 13:05
 */
public final class Z3 {

    private final Context context;
    private final Solver solver;
    private final ArrayList<String> code;

    public Z3() {
        LibraryLinker.loadDirectoryLibraries(new File("lib/z3"));
        this.context = new Context();
        this.solver = getContext().mkSolver();
        this.code = new ArrayList<>();
    }

    public void setCode(ABoolExpr expression) {
        reset();
        Stream.of(SMTLibFormatter.format(expression).split(NEW_LINE)).forEach(code::add);
    }

    public Status checkSAT() {
        try {
            getCode().add("(check-sat)");
            getSolver().add(getContext().parseSMTLIB2String(getCode().stream().collect(Collectors.joining(Chars.NEW_LINE)), null, null, null, null));
            return getSolver().check();
        } catch (Z3Exception e) {
            throw new Error("SMT-Lib2 code cannot be parsed by z3." + NEW_LINE + "SMT-Lib2 code was :" + NEW_LINE + getCode().stream().collect(Collectors.joining(NEW_LINE)));
        }
    }

    private void reset() {
        code.clear();
        solver.reset();
    }

    public Model getModel() {
        return new Model(this, getSolver().getModel());
    }

    public ArrayList<String> getCode() {
        return code;
    }

    public Context getContext() {
        return context;
    }

    private Solver getSolver() {
        return solver;
    }

}
