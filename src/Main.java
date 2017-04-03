import algorithms.ChinesePostmanPathsComputer;
import algorithms.ConnectedATSComputer;
import algorithms.EUAComputer;
import algorithms.UUAComputer;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ComputerResult;
import algorithms.statistics.ATSStatistics;
import algorithms.utilities.AbstractStatesComputer;
import eventb.AEventBObject;
import eventb.Machine;
import eventb.exprs.bool.Predicate;
import eventb.graphs.ATransition;
import eventb.graphs.AbstractState;
import eventb.graphs.ConcreteTransition;
import eventb.parsers.EventBParser;
import utilities.sets.Tuple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static algorithms.heuristics.EEUAComputerHeuristics.ORDERING_COLORATION;
import static java.util.regex.Pattern.quote;
import static utilities.Chars.EQ_DEF;
import static utilities.Chars.NEW_LINE;

public class Main {

    public static void main(String[] args) {
        /*Main main = new Main();
        if (args.length == 2) {
            File ebmFile = new File(args[0]);
            File abstractionPredicatesFile = new File(args[1]);
            if (!ebmFile.exists()) {
                InputStream link = main.getClass().getResourceAsStream(ebmFile.getAbsolutePath());
                if (link != null) {
                    try {
                        Files.copy(link, ebmFile.getAbsoluteFile().toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new Error("Unable to find EBM file \"" + ebmFile.getAbsoluteFile() + "\".");
                }
            }
            if (!abstractionPredicatesFile.exists()) {
                InputStream link = main.getClass().getResourceAsStream(abstractionPredicatesFile.getAbsolutePath());
                if (link != null) {
                    try {
                        Files.copy(link, abstractionPredicatesFile.getAbsoluteFile().toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new Error("Unable to find AP file \"" + abstractionPredicatesFile.getAbsoluteFile() + "\".");
                }
            }
            Machine machine = EventBParser.parseMachine(ebmFile);
            LinkedHashSet<Predicate> abstractionPredicates = EventBParser.parseAbstractionPredicates(abstractionPredicatesFile);
            go(machine, abstractionPredicates);
            System.out.println("Outputs for \"" + ebmFile + "\" have been written in resources/eventb/" + machine.getName() + "/outputs");
        } else {
            throw new Error(
                    "StraTest requires two arguments.\n" +
                            "Usage: $> java -jar StraTest.jar [EBM file (.ebm)] [Abstraction Predicates File (.ap)]\n" +
                            "Example: $> java -jar StraTest.jar resources/eventb/threeBatteries/threeBatteries.ebm resources/eventb/threeBatteries/threeBatteries_default.ap"
            );
        }*/
        /*Tuple<Machine, LinkedHashSet<Predicate>> example = get("coffeeMachine", 1);
        go(example.getFirst(), example.getSecond());
        System.exit(42);*/
        List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> examples = getExamplesTASE17();
        examples.forEach(tuple -> tuple.getSecond().forEach(abstractionPredicatesSet -> {
            System.out.println(tuple.getFirst().getName());
            go(tuple.getFirst(), abstractionPredicatesSet);
        }));
    }

    private static void go(Machine machine, LinkedHashSet<Predicate> abstractionPredicates) {
        List<Integer> filterAndOrder = Arrays.asList(1, 4, 9, 10, 12, 2, 3, 5, 7, 21, 22, 28);
        LinkedHashSet<AbstractState> abstractStates = new AbstractStatesComputer(machine.getInvariant(), abstractionPredicates).compute().getResult();
        ComputerResult<ApproximatedTransitionSystem> eua = new EUAComputer(machine, abstractStates, ORDERING_COLORATION).compute();
        ComputerResult<ApproximatedTransitionSystem> uua = new UUAComputer(machine, eua.getResult()).compute();
        System.out.println(new ATSStatistics(eua.getResult()).getRowRepresentation(filterAndOrder) + " " + eua.getComputationTime());
        System.out.println(new ATSStatistics(uua.getResult()).getRowRepresentation(filterAndOrder) + " " + uua.getComputationTime());
        /*System.out.println(machine);
        System.out.println(new ATSStatistics(eua.getResult()).getRowRepresentation(filterAndOrder) + " " + eua.getComputationTime());
        System.out.println(new ATSStatistics(uua.getResult()).getRowRepresentation(filterAndOrder) + " " + uua.getComputationTime());
        System.out.println(eua.getResult().get3MTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];"));
        System.out.println(eua.getResult().getCTS().getCorrespondingGraphvizGraph());
        System.out.println(eua.getResult().getCTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];"));
        System.out.println(uua.getResult().getCTS().getCorrespondingGraphvizGraph());
        System.out.println(uua.getResult().getCTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];"));*/
        File outputsDirectory = new File("resources/eventb/" + machine.getName() + "/outputs");
        boolean outputDirectoryExists = outputsDirectory.exists() || outputsDirectory.mkdirs();
        if (outputDirectoryExists) {
            try {
                Files.write(Paths.get(outputsDirectory + "/" + machine.getName() + ".mch"), machine.toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/inputs_statistics.txt"), ("" +
                        "# events: " + machine.getEvents().size() + "\n" +
                        "# abstraction predicates: " + abstractionPredicates.size() + "\n" +
                        "\n" +
                        "Abstraction predicates: \n" +
                        abstractionPredicates.stream().map(predicate -> "\t" + predicate).collect(Collectors.joining("\n")) + "\n" +
                        "").getBytes()
                );
                Files.write(Paths.get(outputsDirectory + "/3MTS_full.dot"), eua.getResult().get3MTS().getCorrespondingGraphvizGraph().toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/3MTS_small.dot"), eua.getResult().get3MTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];").getBytes());
                Files.write(Paths.get(outputsDirectory + "/EUA_full.dot"), eua.getResult().getCTS().getCorrespondingGraphvizGraph().toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/EUA_small.dot"), eua.getResult().getCTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];").getBytes());
                Files.write(Paths.get(outputsDirectory + "/UUA_full.dot"), uua.getResult().getCTS().getCorrespondingGraphvizGraph().toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/UUA_small.dot"), uua.getResult().getCTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];").getBytes());
                Files.write(Paths.get(outputsDirectory + "/EUA_statistics.txt"), new ATSStatistics(eua.getResult()).toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/UUA_statistics.txt"), new ATSStatistics(uua.getResult()).toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/abstractionPredicates.ap"), abstractionPredicates.stream().map(AEventBObject::toString).collect(Collectors.joining(NEW_LINE)).getBytes());
                ApproximatedTransitionSystem connectedEUA = new ConnectedATSComputer(eua.getResult()).compute().getResult();
                ComputerResult<List<List<ConcreteTransition>>> testsEUA = new ChinesePostmanPathsComputer(connectedEUA.getCTS().getC0().iterator().next(), connectedEUA.getCTS().getC(), connectedEUA.getCTS().getDeltaC()).compute();
                Files.write(Paths.get(outputsDirectory + "/EUA_tests.txt"), testsEUA.getResult().stream().map(test -> test.stream().map(ATransition::toString).collect(Collectors.joining(NEW_LINE))).collect(Collectors.joining(NEW_LINE + NEW_LINE)).getBytes());
                ApproximatedTransitionSystem connectedUUA = new ConnectedATSComputer(uua.getResult()).compute().getResult();
                ComputerResult<List<List<ConcreteTransition>>> testsUUA = new ChinesePostmanPathsComputer(connectedUUA.getCTS().getC0().iterator().next(), connectedUUA.getCTS().getC(), connectedUUA.getCTS().getDeltaC()).compute();
                Files.write(Paths.get(outputsDirectory + "/UUA_tests.txt"), testsUUA.getResult().stream().map(test -> test.stream().map(ATransition::toString).collect(Collectors.joining(NEW_LINE))).collect(Collectors.joining(NEW_LINE + NEW_LINE)).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new Error("Unable to create output directory \"" + outputsDirectory + "\".");
        }
    }

    private static List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> getExamplesTASE17() {
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
        Machine phone = EventBParser.parseMachine(new File("resources/eventb/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("resources/eventb/phone/phone_2post.ap"));
        List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> examples = new ArrayList<>();
        examples.add(new Tuple<>(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post, threeBatteries_default2)));
        //examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post)));
        examples.add(new Tuple<>(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post)));
        examples.add(new Tuple<>(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post)));
        examples.add(new Tuple<>(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post)));
        return examples;
    }

    private static List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> getExamples() {
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
        List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> examples = new ArrayList<>();
        examples.add(new Tuple<>(simple, Collections.singletonList(simple_1)));
        examples.add(new Tuple<>(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post, threeBatteries_default2)));
        //examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post)));
        examples.add(new Tuple<>(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post)));
        examples.add(new Tuple<>(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post)));
        examples.add(new Tuple<>(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post)));
        examples.add(new Tuple<>(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard)));
        examples.add(new Tuple<>(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post)));
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
