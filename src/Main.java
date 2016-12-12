import eventb.Event;
import eventb.Machine;
import eventb.parsers.EventBParser;

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
    }

}
