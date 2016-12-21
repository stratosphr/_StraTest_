import algorithms.utilities.AbstractStateComputer;
import algorithms.utilities.AbstractStatesComputer;
import eventb.Machine;
import eventb.exprs.bool.*;
import eventb.graphs.AbstractState;
import eventb.parsers.EventBParser;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_default.ap"));
        go(getMachine("threeBatteries"), threeBatteries_default);
    }

    private static Machine getMachine(String machineName) {
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventb/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_1post.ap"));
        Machine carAlarm = EventBParser.parseMachine(new File("resources/eventb/carAlarm/carAlarm.ebm"));
        LinkedHashSet<Predicate> carAlarm_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/carAlarm/carAlarm_1guard.ap"));
        LinkedHashSet<Predicate> carAlarm_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/carAlarm/carAlarm_2guard.ap"));
        LinkedHashSet<Predicate> carAlarm_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/carAlarm/carAlarm_1post.ap"));
        LinkedHashSet<Predicate> carAlarm_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/carAlarm/carAlarm_2post.ap"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("resources/eventb/coffeeMachine/coffeeMachine.ebm"));
        LinkedHashSet<Predicate> coffeeMachine_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/coffeeMachine/coffeeMachine_1guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/coffeeMachine/coffeeMachine_2guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/coffeeMachine/coffeeMachine_1post.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/coffeeMachine/coffeeMachine_2post.ap"));
        Machine creditCard = EventBParser.parseMachine(new File("resources/eventb/creditCard/creditCard.ebm"));
        LinkedHashSet<Predicate> creditCard_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/creditCard/creditCard_1guard.ap"));
        LinkedHashSet<Predicate> creditCard_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/creditCard/creditCard_2guard.ap"));
        LinkedHashSet<Predicate> creditCard_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/creditCard/creditCard_1post.ap"));
        LinkedHashSet<Predicate> creditCard_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/creditCard/creditCard_2post.ap"));
        Machine frontWiper = EventBParser.parseMachine(new File("resources/eventb/frontWiper/frontWiper.ebm"));
        LinkedHashSet<Predicate> frontWiper_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/frontWiper/frontWiper_1guard.ap"));
        LinkedHashSet<Predicate> frontWiper_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/frontWiper/frontWiper_2guard.ap"));
        Machine phone = EventBParser.parseMachine(new File("resources/eventb/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_2post.ap"));
        Map<Machine, List<LinkedHashSet<Predicate>>> examples = new LinkedHashMap<>();
        examples.put(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post));
        examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post));
        examples.put(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post));
        examples.put(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post));
        examples.put(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard));
        examples.put(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post));
        return examples.keySet().stream().filter(machine -> machine.getName().equals(machineName)).findFirst().orElse(null);
    }

    private static void go(Machine machine, LinkedHashSet<Predicate> abstractionPredicates) {
        LinkedHashSet<AbstractState> abstractStates = new AbstractStatesComputer(machine.getInvariant(), abstractionPredicates).compute();
        abstractionPredicates.forEach(System.out::println);
        System.out.println();
        abstractStates.forEach(System.out::println);
        ABoolExpr c = new And(new True());
        AbstractState abstractState = new AbstractStateComputer(c, machine.getInvariant(), abstractStates).compute();
        System.out.println();
        System.out.println(abstractState);
    }

}
