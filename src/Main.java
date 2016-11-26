import eventb.events.*;
import eventb.exprs.arith.Int;
import eventb.exprs.arith.IntVariable;
import eventb.exprs.arith.Sum;
import eventb.exprs.bool.Equals;
import utilities.xml.XMLDocument;
import utilities.xml.XMLNode;
import utilities.xml.XMLParser;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        //XMLDocument parse = XMLParser.parse(new File("resources/eventb/threeBatteries/threeBatteries.ebm"));
        XMLDocument document = XMLParser.parse(new File("resources/eventb/simple/simple.ebm"));
        XMLNode root = document.getRoot();
        Event e = new Event("ev", new Any(
                new Equals(new IntVariable("v1"), new IntVariable("v2")),
                new Select(
                        new Equals(new IntVariable("v1"), new Int(10)),
                        new Parallel(
                                new Assignment(new IntVariable("var"), new Sum(new IntVariable("v2"), new Int(10))),
                                new Assignment(new IntVariable("var2"), new Sum(new IntVariable("v1"), new Int(10))),
                                new Choice(
                                        new Assignment(new IntVariable("v1"), new Int(0)),
                                        new Select(
                                                new Equals(new IntVariable("var"), new Int(10)),
                                                new Assignment(new IntVariable("v1"), new Int(0))
                                        ),
                                        new Parallel(
                                                new Assignment(new IntVariable("var"), new Sum(new IntVariable("v2"), new Int(10))),
                                                new Assignment(new IntVariable("var2"), new Sum(new IntVariable("v1"), new Int(10)))
                                        )
                                )
                        )
                ),
                new IntVariable("v1"),
                new IntVariable("v2")
        ));
        System.out.println(e);
    }

}
