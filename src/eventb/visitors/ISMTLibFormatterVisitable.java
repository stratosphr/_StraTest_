package eventb.visitors;

import eventb.exprs.arith.IntVariable;

import java.util.LinkedHashSet;

/**
 * Created by gvoiron on 24/11/16.
 * Time : 16:25
 */
public interface ISMTLibFormatterVisitable {

    String accept(SMTLibFormatter visitor);

    LinkedHashSet<IntVariable> getVariables();

}
