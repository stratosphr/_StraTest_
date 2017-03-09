import algorithms.EUAComputer;
import algorithms.NewUUAComputer;
import algorithms.heuristics.EEUAComputerHeuristics;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ComputerResult;
import algorithms.statistics.ATSStatistics;
import algorithms.utilities.AbstractStatesComputer;
import eventb.Machine;
import eventb.exprs.bool.Predicate;
import eventb.graphs.AbstractState;
import eventb.parsers.EventBParser;
import utilities.sets.Tuple;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Tuple<Machine, LinkedHashSet<Predicate>> example = get("threeBatteries", 0);
        go(example.getFirst(), example.getSecond());
        System.exit(42);
        Map<Machine, List<LinkedHashSet<Predicate>>> examples = getExamples();
        examples.forEach((machine, abstractionPredicateSets) -> abstractionPredicateSets.forEach(abstractionPredicateSet -> {
            System.out.println(machine.getName());
            go(machine, abstractionPredicateSet);
        }));
    }

    private static void go(Machine machine, LinkedHashSet<Predicate> abstractionPredicates) {
        List<Integer> filterAndOrder = Arrays.asList(1, 2, 3, 4, 5, 6, 17, 19);
        System.out.println("#Ev: " + machine.getEvents().size());
        System.out.println("#AP: " + abstractionPredicates.size());
        ComputerResult<LinkedHashSet<AbstractState>> asResult = new AbstractStatesComputer(machine.getInvariant(), abstractionPredicates).compute();
        LinkedHashSet<AbstractState> abstractStates = asResult.getResult();

        // WARMING STEP *************** //
        /*ComputerResult<ApproximatedTransitionSystem> euaResult = new EUAComputer(machine, abstractStates, EEUAComputerHeuristics.ORDERING_COLORATION).compute();
        ApproximatedTransitionSystem eua = euaResult.getResult();*/
        //System.out.println(new ATSStatistics(eua).getRowRepresentation(filterAndOrder) + " " + euaResult.getComputationTime());
        // END OF WARMING STEP ******** //

        /*ComputerResult<ApproximatedTransitionSystem> eua0Result = new NewEUAComputer(machine, abstractStates).compute();
        ApproximatedTransitionSystem eua0 = eua0Result.getResult();
        System.out.println(new ATSStatistics(eua0).getRowRepresentation(filterAndOrder) + " " + eua0Result.getComputationTime());*/

        /*ComputerResult<ApproximatedTransitionSystem> eua0Result = new EUAComputer(machine, abstractStates, EEUAComputerHeuristics.NO_ORDERING_NO_COLORATION).compute();
        ApproximatedTransitionSystem eua0 = eua0Result.getResult();
        System.out.println(new ATSStatistics(eua0).getRowRepresentation(filterAndOrder) + " " + eua0Result.getComputationTime());

        ComputerResult<ApproximatedTransitionSystem> eua1Result = new EUAComputer(machine, abstractStates, EEUAComputerHeuristics.ORDERING_NO_COLORATION).compute();
        ApproximatedTransitionSystem eua1 = eua1Result.getResult();
        System.out.println(new ATSStatistics(eua1).getRowRepresentation(filterAndOrder) + " " + eua1Result.getComputationTime());

        ComputerResult<ApproximatedTransitionSystem> eua2Result = new EUAComputer(machine, abstractStates, EEUAComputerHeuristics.NO_ORDERING_COLORATION).compute();
        ApproximatedTransitionSystem eua2 = eua2Result.getResult();
        System.out.println(new ATSStatistics(eua2).getRowRepresentation(filterAndOrder) + " " + eua2Result.getComputationTime());*/

        ComputerResult<ApproximatedTransitionSystem> eua3Result = new EUAComputer(machine, abstractStates, EEUAComputerHeuristics.ORDERING_COLORATION).compute();
        System.out.println(eua3Result.getResult().getTriModalTransitionSystem().getCorrespondingGraphvizGraph());
        /*System.out.println(eua3Result.getResult().getConcreteTransitionSystem().getCorrespondingGraphvizGraph());*/
        System.out.println(new ATSStatistics(eua3Result.getResult()).getRowRepresentation(filterAndOrder) + " " + eua3Result.getComputationTime());
        ComputerResult<ApproximatedTransitionSystem> uua3Result = new NewUUAComputer(machine, eua3Result.getResult()).compute();
        //System.out.println(uua3Result.getResult().getConcreteTransitionSystem().getCorrespondingGraphvizGraph());
        System.out.println(new ATSStatistics(uua3Result.getResult()).getRowRepresentation(filterAndOrder) + " " + uua3Result.getComputationTime());

        /*ComputerResult<ApproximatedTransitionSystem> eua0Result = new EUAComputer(machine, abstractStates, NO_ORDERING_NO_COLORATION).compute();
        ApproximatedTransitionSystem eua0 = eua0Result.getResult();
        System.out.println(new ATSStatistics(eua0).getRowRepresentation(filterAndOrder) + " " + eua0Result.getComputationTime());

        ComputerResult<ApproximatedTransitionSystem> eua1Result = new EUAComputer(machine, abstractStates, tuple -> {
            ArrayList<AbstractState> orderedAbstractStates = new ArrayList<>();
            orderedAbstractStates.add(tuple.getFirst());
            orderedAbstractStates.addAll(tuple.getSecond());
            Collections.reverse(orderedAbstractStates);
            return orderedAbstractStates;
        }, events -> {
            ArrayList<Event> events1 = new ArrayList<>(events);
            Collections.sort(events1);
            Collections.reverse(events1);
            return events1;
        }, ORDERING_NO_COLORATION).compute();
        ApproximatedTransitionSystem eua1 = eua1Result.getResult();
        System.out.println(new ATSStatistics(eua1).getRowRepresentation(filterAndOrder) + " " + eua1Result.getComputationTime());*/

        /*ComputerResult<ApproximatedTransitionSystem> eua2Result = new EUAComputer(machine, abstractStates, ORDERING_COLORATION).compute();
        ApproximatedTransitionSystem eua2 = eua2Result.getResult();
        System.out.println(new ATSStatistics(eua2).getRowRepresentation(filterAndOrder) + " " + eua2Result.getComputationTime());*/

        /*System.out.println("---------------------------------------------------");
        /*ApproximatedTransitionSystem uua = new UUAComputer(machine, eua).compute_();
        DirectedGraphvizGraph uuaGraph = uua.getTriModalTransitionSystem().getCorrespondingGraphvizGraph();
        uua.getConcreteTransitionSystem().getDeltaC().forEach(System.out::println);
        System.out.println();
        System.out.println();*/
    }

    private static Map<Machine, List<LinkedHashSet<Predicate>>> getExamples() {
        Machine simple = EventBParser.parseMachine(new File("resources/eventb/simple/simple.ebm"));
        LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("resources/eventb/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventb/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_1post.ap"));
        LinkedHashSet<Predicate> threeBatteries_default2 = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_default2.ap"));
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
        examples.put(simple, Collections.singletonList(simple_1));
        examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post, threeBatteries_default2));
        examples.put(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post));
        examples.put(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post));
        examples.put(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post));
        examples.put(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard));
        examples.put(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post));
        return examples;
    }

    private static Map<Machine, List<LinkedHashSet<Predicate>>> getExamplesHuge() {
        Machine simple = EventBParser.parseMachine(new File("resources/eventbhuge/simple/simple.ebm"));
        LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventbhuge/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_1post.ap"));
        Machine carAlarm = EventBParser.parseMachine(new File("resources/eventbhuge/carAlarm/carAlarm.ebm"));
        LinkedHashSet<Predicate> carAlarm_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_1guard.ap"));
        LinkedHashSet<Predicate> carAlarm_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_2guard.ap"));
        LinkedHashSet<Predicate> carAlarm_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_1post.ap"));
        LinkedHashSet<Predicate> carAlarm_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_2post.ap"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("resources/eventbhuge/coffeeMachine/coffeeMachine.ebm"));
        LinkedHashSet<Predicate> coffeeMachine_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_1guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_2guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_1post.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_2post.ap"));
        Machine creditCard = EventBParser.parseMachine(new File("resources/eventbhuge/creditCard/creditCard.ebm"));
        LinkedHashSet<Predicate> creditCard_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_1guard.ap"));
        LinkedHashSet<Predicate> creditCard_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_2guard.ap"));
        LinkedHashSet<Predicate> creditCard_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_1post.ap"));
        LinkedHashSet<Predicate> creditCard_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_2post.ap"));
        Machine frontWiper = EventBParser.parseMachine(new File("resources/eventbhuge/frontWiper/frontWiper.ebm"));
        LinkedHashSet<Predicate> frontWiper_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/frontWiper/frontWiper_1guard.ap"));
        LinkedHashSet<Predicate> frontWiper_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/frontWiper/frontWiper_2guard.ap"));
        Machine phone = EventBParser.parseMachine(new File("resources/eventbhuge/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_2post.ap"));
        Map<Machine, List<LinkedHashSet<Predicate>>> examples = new LinkedHashMap<>();
        //examples.put(simple, Collections.singletonList(simple_1));
        examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post));
        examples.put(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post));
        examples.put(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post));
        //examples.put(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post));
        examples.put(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post));
        examples.put(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard));
        return examples;
    }

    private static Map<Machine, List<LinkedHashSet<Predicate>>> getExamplesHugePSI() {
        //Machine simple = EventBParser.parseMachine(new File("resources/eventbhuge/simple/simple.ebm"));
        //LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventbhuge/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_1guard.ap"));
        //LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_2guard.ap"));
        //LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_1post.ap"));
        Machine carAlarm = EventBParser.parseMachine(new File("resources/eventbhuge/carAlarm/carAlarm.ebm"));
        LinkedHashSet<Predicate> carAlarm_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_1guard.ap"));
        LinkedHashSet<Predicate> carAlarm_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_2guard.ap"));
        //LinkedHashSet<Predicate> carAlarm_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_1post.ap"));
        //LinkedHashSet<Predicate> carAlarm_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_2post.ap"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("resources/eventbhuge/coffeeMachine/coffeeMachine.ebm"));
        LinkedHashSet<Predicate> coffeeMachine_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_1guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_2guard.ap"));
        //LinkedHashSet<Predicate> coffeeMachine_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_1post.ap"));
        //LinkedHashSet<Predicate> coffeeMachine_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_2post.ap"));
        /*Machine creditCard = EventBParser.parseMachine(new File("resources/eventbhuge/creditCard/creditCard.ebm"));
        LinkedHashSet<Predicate> creditCard_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_1guard.ap"));
        LinkedHashSet<Predicate> creditCard_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_2guard.ap"));
        LinkedHashSet<Predicate> creditCard_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_1post.ap"));
        LinkedHashSet<Predicate> creditCard_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_2post.ap"));*/
        Machine frontWiper = EventBParser.parseMachine(new File("resources/eventbhuge/frontWiper/frontWiper.ebm"));
        LinkedHashSet<Predicate> frontWiper_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/frontWiper/frontWiper_1guard.ap"));
        LinkedHashSet<Predicate> frontWiper_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/frontWiper/frontWiper_2guard.ap"));
        /*Machine phone = EventBParser.parseMachine(new File("resources/eventbhuge/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_2post.ap"));*/
        Map<Machine, List<LinkedHashSet<Predicate>>> examples = new LinkedHashMap<>();
        //examples.put(simple, Collections.singletonList(simple_1));
        examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard));
        //examples.put(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post));
        examples.put(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard));
        //examples.put(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post));
        examples.put(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard));
        examples.put(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard));
        return examples;
    }

    private static Tuple<Machine, LinkedHashSet<Predicate>> get(String machineName, int abstractionPredicatesSetIndex) {
        Machine simple = EventBParser.parseMachine(new File("resources/eventb/simple/simple.ebm"));
        LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("resources/eventb/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventb/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_1post.ap"));
        LinkedHashSet<Predicate> threeBatteries_default2 = EventBParser.parseAbstractionPredicates(new File("resources/eventb/threeBatteries/threeBatteries_default2.ap"));
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
        examples.put(simple, Collections.singletonList(simple_1));
        System.out.println(threeBatteries_default2);
        examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post, threeBatteries_default2));
        examples.put(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post));
        examples.put(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post));
        examples.put(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post));
        examples.put(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard));
        examples.put(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post));
        Machine machine = examples.keySet().stream().filter(machine1 -> machine1.getName().equals(machineName)).findFirst().orElse(null);
        LinkedHashSet<Predicate> abstractionPredicatesSet = examples.get(machine).get(abstractionPredicatesSetIndex);
        if (machine == null) {
            throw new Error("Unable to find machine with name \"" + machineName + "\"");
        }
        if (abstractionPredicatesSet == null) {
            throw new Error("Unable to find abstraction predicate with index \"" + abstractionPredicatesSetIndex + "\" for machine \"" + machineName + "\".");
        }
        return new Tuple<>(machine, abstractionPredicatesSet);
    }

    private static Tuple<Machine, LinkedHashSet<Predicate>> getHuge(String machineName, int abstractionPredicatesSetIndex) {
        Machine simple = EventBParser.parseMachine(new File("resources/eventbhuge/simple/simple.ebm"));
        LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("resources/eventbhuge/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/threeBatteries/threeBatteries_1post.ap"));
        Machine carAlarm = EventBParser.parseMachine(new File("resources/eventbhuge/carAlarm/carAlarm.ebm"));
        LinkedHashSet<Predicate> carAlarm_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_1guard.ap"));
        LinkedHashSet<Predicate> carAlarm_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_2guard.ap"));
        LinkedHashSet<Predicate> carAlarm_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_1post.ap"));
        LinkedHashSet<Predicate> carAlarm_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/carAlarm/carAlarm_2post.ap"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("resources/eventbhuge/coffeeMachine/coffeeMachine.ebm"));
        LinkedHashSet<Predicate> coffeeMachine_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_1guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_2guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_1post.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/coffeeMachine/coffeeMachine_2post.ap"));
        Machine creditCard = EventBParser.parseMachine(new File("resources/eventbhuge/creditCard/creditCard.ebm"));
        LinkedHashSet<Predicate> creditCard_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_1guard.ap"));
        LinkedHashSet<Predicate> creditCard_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_2guard.ap"));
        LinkedHashSet<Predicate> creditCard_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_1post.ap"));
        LinkedHashSet<Predicate> creditCard_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/creditCard/creditCard_2post.ap"));
        Machine frontWiper = EventBParser.parseMachine(new File("resources/eventbhuge/frontWiper/frontWiper.ebm"));
        LinkedHashSet<Predicate> frontWiper_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/frontWiper/frontWiper_1guard.ap"));
        LinkedHashSet<Predicate> frontWiper_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/frontWiper/frontWiper_2guard.ap"));
        Machine phone = EventBParser.parseMachine(new File("resources/eventbhuge/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventbhuge/phone/phone_2post.ap"));
        Map<Machine, List<LinkedHashSet<Predicate>>> examples = new LinkedHashMap<>();
        examples.put(simple, Collections.singletonList(simple_1));
        examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post));
        examples.put(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post));
        examples.put(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post));
        examples.put(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post));
        examples.put(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard));
        examples.put(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post));
        Machine machine = examples.keySet().stream().filter(machine1 -> machine1.getName().equals(machineName)).findFirst().orElse(null);
        LinkedHashSet<Predicate> abstractionPredicatesSet = examples.get(machine).get(abstractionPredicatesSetIndex);
        if (machine == null) {
            throw new Error("Unable to find machine with name \"" + machineName + "\"");
        }
        if (abstractionPredicatesSet == null) {
            throw new Error("Unable to find abstraction predicate with index \"" + abstractionPredicatesSetIndex + "\" for machine \"" + machineName + "\".");
        }
        return new Tuple<>(machine, abstractionPredicatesSet);
    }

}
