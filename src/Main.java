import com.microsoft.z3.Status;
import eventb.Event;
import eventb.Machine;
import eventb.exprs.arith.Int;
import eventb.exprs.arith.IntVariable;
import eventb.exprs.bool.ABoolExpr;
import eventb.exprs.bool.And;
import eventb.exprs.bool.Equals;
import eventb.parsers.EventBParser;
import solvers.z3.Z3;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        /*Machine carAlarm = EventBParser.parseMachine(new File("resources/eventb/carAlarm/carAlarm.ebm"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("resources/eventb/coffeeMachine/coffeeMachine.ebm"));
        Machine creditCard = EventBParser.parseMachine(new File("resources/eventb/creditCard/creditCard.ebm"));
        Machine frontWiper = EventBParser.parseMachine(new File("resources/eventb/frontWiper/frontWiper.ebm"));
        Machine phone = EventBParser.parseMachine(new File("resources/eventb/phone/phone.ebm"));*/
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventb/threeBatteries/threeBatteries.ebm"));
        //Machine simple = EventBParser.parseMachine(new File("resources/eventb/simple/simple.ebm"));
        /*List<Machine> machines = Arrays.asList(carAlarm, coffeeMachine, creditCard, frontWiper, phone, threeBatteries, simple);
        for (Machine machine : machines) {
            for (Event event : machine.getEvents()) {
                System.out.println(event.getSubstitution().getPrd(machine));
            }
        }*/
        Event tic = threeBatteries.getEvents().stream().filter(event -> event.getName().equals("Tic")).findFirst().orElse(null);
        Event repair = threeBatteries.getEvents().stream().filter(event -> event.getName().equals("Repair")).findFirst().orElse(null);
        Event commute = threeBatteries.getEvents().stream().filter(event -> event.getName().equals("Commute")).findFirst().orElse(null);
        Event fail = threeBatteries.getEvents().stream().filter(event -> event.getName().equals("Fail")).findFirst().orElse(null);
        Z3 z3 = new Z3();
        ABoolExpr c = new And(
                new Equals(
                        new IntVariable("h"),
                        new Int(1)
                )
        );
        ABoolExpr c_ = new And(
                new Equals(
                        new IntVariable("h"),
                        new Int(0)
                )
        ).prime();
        z3.setCode(new And(
                threeBatteries.getInvariant(),
                threeBatteries.getInvariant().prime(),
                c,
                tic.getSubstitution().getPrd(threeBatteries),
                c_
        ));
        Status status = z3.checkSAT();
        if (status == Status.SATISFIABLE) {
            /*for (String s : z3.getCode()) {
                System.out.println(s);
            }*/
            System.out.println(z3.getModel());
        } else {
            System.out.println(status);
        }
    }

}
