import algorithms.FinalRelevanceComputer;
import algorithms.NewEUAComputer;
import algorithms.outputs.ApproximatedTransitionSystem;
import algorithms.outputs.ComputerResult;
import algorithms.statistics.ATSStatistics;
import algorithms.utilities.AbstractStatesComputer;
import algorithms.utilities.ChinesePostmanPathsComputer;
import algorithms.utilities.ConnectedATSComputer;
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
            System.out.println("Outputs for \"" + ebmFile + "\" have been written in resources/eventb/" + machine.getName() + "/outputs.");
        } else {
            throw new Error(
                    "StraTest requires two arguments.\n" +
                            "Usage: $> java -jar StraTest.jar [EBM file (.ebm)] [Abstraction Predicates File (.ap)]\n" +
                            "Example: $> java -jar StraTest.jar resources/eventb/threeBatteries/threeBatteries.ebm resources/eventb/threeBatteries/threeBatteries_default.ap"
            );
        }*/

        Tuple<Machine, LinkedHashSet<Predicate>> example = get("line14", 0);
        go(example.getFirst(), example.getSecond());
        System.exit(42);

        /*List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> examples = getExamples();
        examples.forEach(tuple -> tuple.getSecond().forEach(abstractionPredicatesSet -> {
            go(tuple.getFirst(), abstractionPredicatesSet);
            System.out.println();
        }));*/

        /*examples = getExamplesHuge();
        examples.forEach(tuple -> tuple.getSecond().forEach(abstractionPredicatesSet -> {
            System.out.println(tuple.getFirst().getName());
            go(tuple.getFirst(), abstractionPredicatesSet);
        }));*/

    }

    private static void go(Machine machine, LinkedHashSet<Predicate> abstractionPredicates) {
        //List<Integer> filterAndOrder = Arrays.asList(1, 4, 2, 3, 5, 7, 17, 22, 25, 26, 28);
        System.out.println(machine.getName());
        System.out.println(machine);
        System.out.println("#Ev: " + machine.getEvents().size());
        System.out.println("#AP: " + abstractionPredicates.size());
        List<Integer> filterAndOrder = Arrays.asList(1, 4, 2, 3, 5, 7, 16, 17, 21, 22, 24, 25, 26, 28);
        LinkedHashSet<AbstractState> abstractStates = new AbstractStatesComputer(machine.getInvariant(), abstractionPredicates).compute().getResult();
        /*ComputerResult<ApproximatedTransitionSystem> mcxp = new EUAComputer(machine, abstractStates, EEUAComputerHeuristics.ORDERING_COLORATION).compute();
        System.out.println(new ATSStatistics(mcxp.getResult()).getRowRepresentation(filterAndOrder) + " " + mcxp.getComputationTime());*/
        ComputerResult<ApproximatedTransitionSystem> mcxp = new NewEUAComputer(machine, abstractStates).compute();
        System.out.println(new ATSStatistics(mcxp.getResult()).getRowRepresentation(filterAndOrder) + " " + mcxp.getComputationTime());
        /*ComputerResult<ApproximatedTransitionSystem> relevance = new NewEUAComputerNoWhile(machine, abstractStates).compute();
        System.out.println(new ATSStatistics(relevance.getResult()).getRowRepresentation(filterAndOrder) + " " + relevance.getComputationTime());*/
        ComputerResult<ApproximatedTransitionSystem> relevance = new FinalRelevanceComputer(machine, mcxp.getResult(), states -> {
            /*ConcreteState c = states.getFirst();
            Integer bell = c.getMapping().get(new IntVariable("Be")).getValue();
            Integer doors = c.getMapping().get(new IntVariable("Do")).getValue();
            Integer ctrl = c.getMapping().get(new IntVariable("AC")).getValue();
            Integer lock = c.getMapping().get(new IntVariable("Lo")).getValue();
            Integer user = c.getMapping().get(new IntVariable("Us")).getValue();
            Integer triggered = c.getMapping().get(new IntVariable("Tr")).getValue();
            Integer moving = c.getMapping().get(new IntVariable("Mv")).getValue();
            Integer warnings = c.getMapping().get(new IntVariable("Wa")).getValue();
            Integer glasses = c.getMapping().get(new IntVariable("Gl")).getValue();
            Integer security = c.getMapping().get(new IntVariable("CS")).getValue();
            Integer lights = c.getMapping().get(new IntVariable("Li")).getValue();
            Integer delay = c.getMapping().get(new IntVariable("De")).getValue();
            ConcreteState c_ = states.getSecond();
            Integer bell_ = c_.getMapping().get(new IntVariable("Be")).getValue();
            Integer doors_ = c_.getMapping().get(new IntVariable("Do")).getValue();
            Integer ctrl_ = c_.getMapping().get(new IntVariable("AC")).getValue();
            Integer lock_ = c_.getMapping().get(new IntVariable("Lo")).getValue();
            Integer user_ = c_.getMapping().get(new IntVariable("Us")).getValue();
            Integer triggered_ = c_.getMapping().get(new IntVariable("Tr")).getValue();
            Integer moving_ = c_.getMapping().get(new IntVariable("Mv")).getValue();
            Integer warnings_ = c_.getMapping().get(new IntVariable("Wa")).getValue();
            Integer glasses_ = c_.getMapping().get(new IntVariable("Gl")).getValue();
            Integer security_ = c_.getMapping().get(new IntVariable("CS")).getValue();
            Integer lights_ = c_.getMapping().get(new IntVariable("Li")).getValue();
            Integer delay_ = c_.getMapping().get(new IntVariable("De")).getValue();*/
            return false;
        }).compute();
        /*ComputerResult<ApproximatedTransitionSystem> relevance = new EUAWithRelevanceGuidedComputer(machine, abstractStates, new IGuidingRelevanceFunction() {
            @Override
            public List<ABoolExpr> apply(ConcreteState c) {
                return Arrays.asList(
                        new GreaterThan(new IntVariable("Portes"), new IntVariable("Portes").prime()),
                        new GreaterThan(new IntVariable("Ctrl_Alarme").prime(), new IntVariable("Ctrl_Alarme")),
                        new GreaterThan(new IntVariable("Serrures").prime(), new IntVariable("Serrures")),
                        new GreaterThan(new IntVariable("Utilisateur"), new IntVariable("Utilisateur").prime()),
                        new GreaterThan(new IntVariable("Alarme").prime(), new IntVariable("Alarme"))
                );
            }
        }).compute();*/
        System.out.println(new ATSStatistics(relevance.getResult()).getRowRepresentation(filterAndOrder) + " " + relevance.getComputationTime());
        /*ComputerResult<ApproximatedTransitionSystem> relevance = new FinalRelevanceComputer(machine, mcxp.getResult(), states -> {
            ConcreteState c = states.getFirst();
            Integer bat1 = c.getMapping().get(new IntVariable("bat1")).getValue();
            Integer bat2 = c.getMapping().get(new IntVariable("bat2")).getValue();
            Integer bat3 = c.getMapping().get(new IntVariable("bat3")).getValue();
            Integer bat4 = c.getMapping().get(new IntVariable("bat4")).getValue();
            Integer bat5 = c.getMapping().get(new IntVariable("bat5")).getValue();
            Integer bat6 = c.getMapping().get(new IntVariable("bat6")).getValue();
            Integer bat7 = c.getMapping().get(new IntVariable("bat7")).getValue();
            Integer bat8 = c.getMapping().get(new IntVariable("bat8")).getValue();
            Integer bat9 = c.getMapping().get(new IntVariable("bat9")).getValue();
            Integer bat10 = c.getMapping().get(new IntVariable("bat10")).getValue();
            ConcreteState c_ = states.getSecond();
            Integer bat1_ = c_.getMapping().get(new IntVariable("bat1")).getValue();
            Integer bat2_ = c_.getMapping().get(new IntVariable("bat2")).getValue();
            Integer bat3_ = c_.getMapping().get(new IntVariable("bat3")).getValue();
            Integer bat4_ = c_.getMapping().get(new IntVariable("bat4")).getValue();
            Integer bat5_ = c_.getMapping().get(new IntVariable("bat5")).getValue();
            Integer bat6_ = c_.getMapping().get(new IntVariable("bat6")).getValue();
            Integer bat7_ = c_.getMapping().get(new IntVariable("bat7")).getValue();
            Integer bat8_ = c_.getMapping().get(new IntVariable("bat8")).getValue();
            Integer bat9_ = c_.getMapping().get(new IntVariable("bat9")).getValue();
            Integer bat10_ = c_.getMapping().get(new IntVariable("bat10")).getValue();
            return bat1 + bat2 + bat3 + bat4 + bat5 + bat6 + bat7 + bat8 + bat9 + bat10 < bat1_ + bat2_ + bat3_ + bat4_ + bat5_ + bat6_ + bat7_ + bat8_ + bat9_ + bat10_;
        }).compute();
        System.out.println(new ATSStatistics(relevance.getResult()).getRowRepresentation(filterAndOrder) + " " + relevance.getComputationTime());*/
        /*ComputerResult<ApproximatedTransitionSystem> gv2 = new NewEUAComputerNew(machine, abstractStates).compute();
        System.out.println(new ATSStatistics(gv2.getResult()).getRowRepresentation(filterAndOrder) + " " + gv2.getComputationTime());*/
        /*ComputerResult<ApproximatedTransitionSystem> gv2 = new NewEUAComputer2(machine, abstractStates).compute();
        System.out.println(new ATSStatistics(gv2.getResult()).getRowRepresentation(filterAndOrder) + " " + gv2.getComputationTime());*/
        /*ComputerResult<ApproximatedTransitionSystem> gv3 = new NewEUAComputer3(machine, abstractStates).compute();
        System.out.println(new ATSStatistics(gv3.getResult()).getRowRepresentation(filterAndOrder) + " " + gv3.getComputationTime());*/
        /*ComputerResult<ApproximatedTransitionSystem> gv = new EUAWithRelevanceMinComputer(machine, abstractStates, states -> {
            ConcreteState cMin = states.getFirst();
            ConcreteState c = states.getSecond();
            Integer minCoffeeLeft = cMin.getMapping().get(new IntVariable("CoffeeLeft")).getValue();
            Integer minBalance = cMin.getMapping().get(new IntVariable("Balance")).getValue();
            Integer minAskCoffee = cMin.getMapping().get(new IntVariable("AskCoffee")).getValue();
            Integer minPot = cMin.getMapping().get(new IntVariable("Pot")).getValue();
            Integer coffeeLeft = c.getMapping().get(new IntVariable("CoffeeLeft")).getValue();
            Integer balance = c.getMapping().get(new IntVariable("Balance")).getValue();
            Integer askCoffee = c.getMapping().get(new IntVariable("AskCoffee")).getValue();
            Integer pot = c.getMapping().get(new IntVariable("Pot")).getValue();
            return coffeeLeft < minCoffeeLeft || balance > minBalance || (askCoffee == 1 && minAskCoffee == 0);
        }).compute();*/
        /*ComputerResult<ApproximatedTransitionSystem> relevance = new EUAWithRelevanceComputer(machine, abstractStates, states -> {
            ConcreteState c = states.getFirst();
            ConcreteState c_ = states.getSecond();
            Integer coffeeLeft = c.getMapping().get(new IntVariable("CoffeeLeft")).getValue();
            Integer balance = c.getMapping().get(new IntVariable("Balance")).getValue();
            Integer askCoffee = c.getMapping().get(new IntVariable("AskCoffee")).getValue();
            Integer pot = c.getMapping().get(new IntVariable("Pot")).getValue();
            Integer status = c.getMapping().get(new IntVariable("Status")).getValue();
            Integer coffeeLeft_ = c_.getMapping().get(new IntVariable("CoffeeLeft")).getValue();
            Integer balance_ = c_.getMapping().get(new IntVariable("Balance")).getValue();
            Integer askCoffee_ = c_.getMapping().get(new IntVariable("AskCoffee")).getValue();
            Integer pot_ = c_.getMapping().get(new IntVariable("Pot")).getValue();
            Integer status_ = c_.getMapping().get(new IntVariable("Status")).getValue();
            //Integer MAX_POT = c_.getMapping().get(new IntVariable("MAX_POT")).getValue();
            //return coffeeLeft_ < coffeeLeft || balance_ > balance || (askCoffee_ == 1 && askCoffee == 0);
            return (status_ == 0 && pot_ > 0) || coffeeLeft_ < coffeeLeft || balance_ > balance || (askCoffee_ == 1 && askCoffee == 0);
        }).compute();*/
        /*ComputerResult<ApproximatedTransitionSystem> gv = new EUAWithRelevanceGuidedComputer(machine, abstractStates, c -> {
            Int coffeeLeft = c.getMapping().get(new IntVariable("CoffeeLeft"));
            Int max_bal = c.getMapping().get(new IntVariable("MAX_BAL"));
            Int balance = c.getMapping().get(new IntVariable("Balance"));
            Int askCoffee = c.getMapping().get(new IntVariable("AskCoffee"));
            AArithExpr coffeeLeft_ = new IntVariable("CoffeeLeft").prime();
            AArithExpr balance_ = new IntVariable("Balance").prime();
            AArithExpr askCoffee_ = new IntVariable("AskCoffee").prime();
            return Arrays.asList(
                    new LowerThan(coffeeLeft_, coffeeLeft),
                    new LowerThan(
                            new Subtraction(max_bal, balance_),
                            new Subtraction(max_bal, balance)
                    ),
                    new LowerThan(
                            new Subtraction(
                                    new Int(1),
                                    askCoffee_
                            ),
                            new Subtraction(
                                    new Int(1),
                                    askCoffee
                            )
                    )
            );
        }).compute();*/
        //System.out.println(new ATSStatistics(relevance.getResult()).getRowRepresentation(filterAndOrder) + " " + relevance.getComputationTime());
        /*ComputerResult<ApproximatedTransitionSystem> gv2 = new NewEUAComputer2(machine, abstractStates).compute();
        System.out.println(new ATSStatistics(gv2.getResult()).getRowRepresentation(filterAndOrder) + " " + gv2.getComputationTime());
        ComputerResult<ApproximatedTransitionSystem> gv3 = new NewEUAComputer3(machine, abstractStates).compute();
        System.out.println(new ATSStatistics(gv3.getResult()).getRowRepresentation(filterAndOrder) + " " + gv3.getComputationTime());*/
        /*ComputerResult<ApproximatedTransitionSystem> rel = new EUAComputer(machine, abstractStates, ORDERING_COLORATION).compute();
        System.out.println(new ATSStatistics(rel.getResult()).getRowRepresentation(filterAndOrder) + " " + rel.getComputationTime());*/
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
                Files.write(Paths.get(outputsDirectory + "/3MTS_full.dot"), mcxp.getResult().get3MTS().getCorrespondingGraphvizGraph().toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/3MTS_small.dot"), mcxp.getResult().get3MTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];").getBytes());
                Files.write(Paths.get(outputsDirectory + "/EUA_full.dot"), mcxp.getResult().getCTS().getCorrespondingGraphvizGraph().toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/EUA_small.dot"), mcxp.getResult().getCTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];").getBytes());
                Files.write(Paths.get(outputsDirectory + "/UUA_full.dot"), relevance.getResult().getCTS().getCorrespondingGraphvizGraph().toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/UUA_small.dot"), relevance.getResult().getCTS().getCorrespondingGraphvizGraph().toString().replaceAll(quote("("), "").replaceAll(" " + EQ_DEF + ".*", "\"];").getBytes());
                Files.write(Paths.get(outputsDirectory + "/EUA_statistics.txt"), new ATSStatistics(mcxp.getResult()).toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/UUA_statistics.txt"), new ATSStatistics(relevance.getResult()).toString().getBytes());
                Files.write(Paths.get(outputsDirectory + "/abstractionPredicates.ap"), abstractionPredicates.stream().map(AEventBObject::toString).collect(Collectors.joining(NEW_LINE)).getBytes());
                ApproximatedTransitionSystem connectedEUA = new ConnectedATSComputer(mcxp.getResult()).compute().getResult();
                ComputerResult<List<List<ConcreteTransition>>> testsEUA = new ChinesePostmanPathsComputer(connectedEUA.getCTS().getC0().iterator().next(), connectedEUA.getCTS().getC(), connectedEUA.getCTS().getDeltaC()).compute();
                Files.write(Paths.get(outputsDirectory + "/EUA_tests.txt"), testsEUA.getResult().stream().map(test -> test.stream().map(ATransition::toString).collect(Collectors.joining(NEW_LINE))).collect(Collectors.joining(NEW_LINE + NEW_LINE)).getBytes());
                ApproximatedTransitionSystem connectedUUA = new ConnectedATSComputer(relevance.getResult()).compute().getResult();
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
        Machine simple = EventBParser.parseMachine(new File("newResources/eventb/simple/simple.ebm"));
        LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("newResources/eventb/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_1post.ap"));
        LinkedHashSet<Predicate> threeBatteries_default2 = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_default2.ap"));
        Machine tenBatteries = EventBParser.parseMachine(new File("newResources/eventb/tenBatteries/tenBatteries.ebm"));
        LinkedHashSet<Predicate> tenBatteries_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/tenBatteries/tenBatteries_default.ap"));
        Machine carAlarm = EventBParser.parseMachine(new File("newResources/eventb/carAlarm/carAlarm.ebm"));
        LinkedHashSet<Predicate> carAlarm_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_1guard.ap"));
        LinkedHashSet<Predicate> carAlarm_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_2guard.ap"));
        LinkedHashSet<Predicate> carAlarm_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_1post.ap"));
        LinkedHashSet<Predicate> carAlarm_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_2post.ap"));
        LinkedHashSet<Predicate> carAlarm_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_default.ap"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("newResources/eventb/coffeeMachine/coffeeMachine.ebm"));
        LinkedHashSet<Predicate> coffeeMachine_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_1guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_2guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_1post.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_2post.ap"));
        Machine creditCard = EventBParser.parseMachine(new File("newResources/eventb/creditCard/creditCard.ebm"));
        LinkedHashSet<Predicate> creditCard_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_1guard.ap"));
        LinkedHashSet<Predicate> creditCard_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_2guard.ap"));
        LinkedHashSet<Predicate> creditCard_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_1post.ap"));
        LinkedHashSet<Predicate> creditCard_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_2post.ap"));
        Machine frontWiper = EventBParser.parseMachine(new File("newResources/eventb/frontWiper/frontWiper.ebm"));
        LinkedHashSet<Predicate> frontWiper_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/frontWiper/frontWiper_1guard.ap"));
        LinkedHashSet<Predicate> frontWiper_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/frontWiper/frontWiper_2guard.ap"));
        Machine phone = EventBParser.parseMachine(new File("newResources/eventb/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_2post.ap"));
        List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> examples = new ArrayList<>();
        examples.add(new Tuple<>(simple, Collections.singletonList(simple_1)));
        examples.add(new Tuple<>(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post, threeBatteries_default2)));
        examples.add(new Tuple<>(tenBatteries, Collections.singletonList(tenBatteries_default)));
        //examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post)));
        examples.add(new Tuple<>(carAlarm, Arrays.asList(carAlarm_default, carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post)));
        examples.add(new Tuple<>(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post)));
        examples.add(new Tuple<>(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post)));
        examples.add(new Tuple<>(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard)));
        examples.add(new Tuple<>(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post)));
        return examples;
    }

    private static List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> getExamplesHuge() {
        Machine simple = EventBParser.parseMachine(new File("newResources/eventbhuge/simple/simple.ebm"));
        LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("newResources/eventbhuge/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/threeBatteries/threeBatteries_1post.ap"));
        Machine carAlarm = EventBParser.parseMachine(new File("newResources/eventbhuge/carAlarm/carAlarm.ebm"));
        LinkedHashSet<Predicate> carAlarm_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/carAlarm/carAlarm_1guard.ap"));
        LinkedHashSet<Predicate> carAlarm_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/carAlarm/carAlarm_2guard.ap"));
        LinkedHashSet<Predicate> carAlarm_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/carAlarm/carAlarm_1post.ap"));
        LinkedHashSet<Predicate> carAlarm_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/carAlarm/carAlarm_2post.ap"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("newResources/eventbhuge/coffeeMachine/coffeeMachine.ebm"));
        LinkedHashSet<Predicate> coffeeMachine_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/coffeeMachine/coffeeMachine_1guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/coffeeMachine/coffeeMachine_2guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/coffeeMachine/coffeeMachine_1post.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/coffeeMachine/coffeeMachine_2post.ap"));
        Machine creditCard = EventBParser.parseMachine(new File("newResources/eventbhuge/creditCard/creditCard.ebm"));
        LinkedHashSet<Predicate> creditCard_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/creditCard/creditCard_1guard.ap"));
        LinkedHashSet<Predicate> creditCard_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/creditCard/creditCard_2guard.ap"));
        LinkedHashSet<Predicate> creditCard_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/creditCard/creditCard_1post.ap"));
        LinkedHashSet<Predicate> creditCard_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/creditCard/creditCard_2post.ap"));
        Machine frontWiper = EventBParser.parseMachine(new File("newResources/eventbhuge/frontWiper/frontWiper.ebm"));
        LinkedHashSet<Predicate> frontWiper_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/frontWiper/frontWiper_1guard.ap"));
        LinkedHashSet<Predicate> frontWiper_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/frontWiper/frontWiper_2guard.ap"));
        Machine phone = EventBParser.parseMachine(new File("newResources/eventbhuge/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventbhuge/phone/phone_2post.ap"));
        List<Tuple<Machine, List<LinkedHashSet<Predicate>>>> examples = new ArrayList<>();
        examples.add(new Tuple<>(simple, Collections.singletonList(simple_1)));
        examples.add(new Tuple<>(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post)));
        //examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post)));
        examples.add(new Tuple<>(carAlarm, Arrays.asList(carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post)));
        examples.add(new Tuple<>(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post)));
        examples.add(new Tuple<>(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post)));
        examples.add(new Tuple<>(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard)));
        examples.add(new Tuple<>(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post)));
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
        Machine simple = EventBParser.parseMachine(new File("newResources/eventb/simple/simple.ebm"));
        LinkedHashSet<Predicate> simple_1 = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/simple/simple_1.ap"));
        Machine threeBatteries = EventBParser.parseMachine(new File("newResources/eventb/threeBatteries/threeBatteries.ebm"));
        LinkedHashSet<Predicate> threeBatteries_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_default.ap"));
        LinkedHashSet<Predicate> threeBatteries_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_1guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_2guard.ap"));
        LinkedHashSet<Predicate> threeBatteries_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_1post.ap"));
        LinkedHashSet<Predicate> threeBatteries_default2 = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/threeBatteries/threeBatteries_default2.ap"));
        Machine tenBatteries = EventBParser.parseMachine(new File("newResources/eventb/tenBatteries/tenBatteries.ebm"));
        LinkedHashSet<Predicate> tenBatteries_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/tenBatteries/tenBatteries_default.ap"));
        Machine carAlarm = EventBParser.parseMachine(new File("newResources/eventb/carAlarm/carAlarm.ebm"));
        LinkedHashSet<Predicate> carAlarm_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_default.ap"));
        LinkedHashSet<Predicate> carAlarm_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_1guard.ap"));
        LinkedHashSet<Predicate> carAlarm_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_2guard.ap"));
        LinkedHashSet<Predicate> carAlarm_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_1post.ap"));
        LinkedHashSet<Predicate> carAlarm_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/carAlarm/carAlarm_2post.ap"));
        Machine coffeeMachine = EventBParser.parseMachine(new File("newResources/eventb/coffeeMachine/coffeeMachine.ebm"));
        LinkedHashSet<Predicate> coffeeMachine_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_1guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_2guard.ap"));
        LinkedHashSet<Predicate> coffeeMachine_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_1post.ap"));
        LinkedHashSet<Predicate> coffeeMachine_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/coffeeMachine/coffeeMachine_2post.ap"));
        Machine creditCard = EventBParser.parseMachine(new File("newResources/eventb/creditCard/creditCard.ebm"));
        LinkedHashSet<Predicate> creditCard_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_1guard.ap"));
        LinkedHashSet<Predicate> creditCard_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_2guard.ap"));
        LinkedHashSet<Predicate> creditCard_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_1post.ap"));
        LinkedHashSet<Predicate> creditCard_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/creditCard/creditCard_2post.ap"));
        Machine frontWiper = EventBParser.parseMachine(new File("newResources/eventb/frontWiper/frontWiper.ebm"));
        LinkedHashSet<Predicate> frontWiper_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/frontWiper/frontWiper_1guard.ap"));
        LinkedHashSet<Predicate> frontWiper_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/frontWiper/frontWiper_2guard.ap"));
        Machine phone = EventBParser.parseMachine(new File("newResources/eventb/phone/phone.ebm"));
        LinkedHashSet<Predicate> phone_1guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_1guard.ap"));
        LinkedHashSet<Predicate> phone_2guard = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_2guard.ap"));
        LinkedHashSet<Predicate> phone_1post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_1post.ap"));
        LinkedHashSet<Predicate> phone_2post = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/phone/phone_2post.ap"));
        Machine line14 = EventBParser.parseMachine(new File("newResources/eventb/line14/line14.ebm"));
        LinkedHashSet<Predicate> line14_default = EventBParser.parseAbstractionPredicates(new File("newResources/eventb/line14/line14_default.ap"));
        Map<Machine, List<LinkedHashSet<Predicate>>> examples = new LinkedHashMap<>();
        examples.put(simple, Collections.singletonList(simple_1));
        examples.put(threeBatteries, Arrays.asList(threeBatteries_default, threeBatteries_1guard, threeBatteries_2guard, threeBatteries_1post, threeBatteries_default2));
        examples.put(tenBatteries, Collections.singletonList(tenBatteries_default));
        examples.put(carAlarm, Arrays.asList(carAlarm_default, carAlarm_1guard, carAlarm_2guard, carAlarm_1post, carAlarm_2post));
        examples.put(coffeeMachine, Arrays.asList(coffeeMachine_1guard, coffeeMachine_2guard, coffeeMachine_1post, coffeeMachine_2post));
        examples.put(creditCard, Arrays.asList(creditCard_1guard, creditCard_2guard, creditCard_1post, creditCard_2post));
        examples.put(frontWiper, Arrays.asList(frontWiper_1guard, frontWiper_2guard));
        examples.put(phone, Arrays.asList(phone_1guard, phone_2guard, phone_1post, phone_2post));
        examples.put(line14, Collections.singletonList(line14_default));
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
