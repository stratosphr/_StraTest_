import eventb.exprs.arith.*;
import eventb.exprs.bool.*;

public class Main {

    public static void main(String[] args) {
        IntVariable v1 = new IntVariable("v1");
        IntVariable v2 = new IntVariable("v2");
        Equals equals = new Equals(v1, v2);
        Sum sum = new Sum(v1, v2);
        Subtraction subtraction = new Subtraction(v1, sum);
        Product product = new Product(sum, subtraction);
        Division division = new Division(subtraction, product);
        System.out.println(sum);
        System.out.println(subtraction);
        System.out.println(product);
        System.out.println(division);
        System.out.println(new And(equals, equals));
        System.out.println(new And());
        System.out.println(equals);
        System.out.println(new Not(new Or(equals, new GreaterOrEqual(v1, v2), new And(new LowerOrEqual(v2, v1), new LowerThan(v1, v2), new GreaterThan(v1, v2)))));
    }

}
