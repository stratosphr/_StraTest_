package solvers.z3;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
import eventb.exprs.arith.AAssignable;
import eventb.exprs.bool.ABoolExpr;
import eventb.visitors.SMTLibFormatter;
import utilities.Chars;
import utilities.LibraryLinker;
import utilities.OS;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static utilities.Chars.NEW_LINE;
import static utilities.OS.EOperatingSystem.WINDOWS;

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
        if (OS.getOperatingSystem() == WINDOWS) {
            UnsatisfiedLinkError error = null;
            try {
                LibraryLinker.loadDirectoryLibraries(new File("lib/z3/win_x86"));
                //System.loadLibrary("libz3");
            } catch (UnsatisfiedLinkError ignored) {
                error = ignored;
            }
            LibraryLinker.unloadDirectoryLibraries(new File("lib/z3/win_x86"));
            try {
                LibraryLinker.loadDirectoryLibraries(new File("lib/z3/win_x64"));
            } catch (UnsatisfiedLinkError ignored) {
                if (error != null) {
                    error.printStackTrace();
                }
                ignored.printStackTrace();
            }
            System.loadLibrary("libz3");
        }
        this.context = new Context();
        this.solver = getContext().mkSolver();
        this.code = new ArrayList<>();
    }

    public void setCode(ABoolExpr expression) {
        reset();
        code.addAll(Arrays.asList(SMTLibFormatter.format(expression).split(NEW_LINE)));
    }

    public Status checkSAT() {
        try {
            getCode().add("(check-sat)");
            getContext().parseSMTLIB2String(getCode().stream().collect(Collectors.joining(Chars.NEW_LINE)), null, null, null, null);
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

    public Model getModel(LinkedHashSet<AAssignable> assignables) {
        return new Model(this, getSolver().getModel(), assignables);
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
