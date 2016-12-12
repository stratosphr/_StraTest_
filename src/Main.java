import eventb.Event;
import eventb.Machine;
import eventb.parsers.EventBParser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Machine carAlarm = EventBParser.parseMachine(new File("resources/eventb/carAlarm/carAlarm.ebm"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("resources/eventb/coffeeMachine/coffeeMachine.ebm"));
        Machine creditCard = EventBParser.parseMachine(new File("resources/eventb/creditCard/creditCard.ebm"));
        Machine frontWiper = EventBParser.parseMachine(new File("resources/eventb/frontWiper/frontWiper.ebm"));
        Machine phone = EventBParser.parseMachine(new File("resources/eventb/phone/phone.ebm"));
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventb/threeBatteries/threeBatteries.ebm"));
        Machine simple = EventBParser.parseMachine(new File("resources/eventb/simple/simple.ebm"));
        List<Machine> machines = Arrays.asList(carAlarm, coffeeMachine, creditCard, frontWiper, phone, threeBatteries, simple);
        for (Machine machine : machines) {
            for (Event event : machine.getEvents()) {
                System.out.println(event.getSubstitution().getPrd(machine));
            }
        }
    }

}
