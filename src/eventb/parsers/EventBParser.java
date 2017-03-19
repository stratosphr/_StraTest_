package eventb.parsers;

import eventb.Event;
import eventb.Machine;
import eventb.exprs.arith.*;
import eventb.exprs.bool.*;
import eventb.exprs.sets.NamedSet;
import eventb.parsers.metamodel.EBMAttributes;
import eventb.parsers.metamodel.EBMEntities;
import eventb.substitutions.*;
import utilities.xml.XMLDocument;
import utilities.xml.XMLNode;
import utilities.xml.XMLParser;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static eventb.parsers.metamodel.EBMAttributes.*;
import static eventb.parsers.metamodel.EBMAttributesValues.TYPE_NATURAL_NUMBERS;
import static eventb.parsers.metamodel.EBMEntities.*;
import static eventb.parsers.metamodel.EventBRegex.IDENTIFIER;

/**
 * Created by gvoiron on 26/11/16.
 * Time : 16:32
 */
public final class EventBParser {

    public static Machine parseMachine(File file) {
        XMLDocument document = XMLParser.parse(file, "MACHINE", new File("resources/eventb/ebm.dtd"), new ErrorHandler(file));
        XMLNode root = document.getRoot();
        String name = root.getAttributes().get(EBMAttributes.NAME);
        LinkedHashSet<NamedSet> sets = new LinkedHashSet<>();
        LinkedHashSet<AAssignable> assignables = parseVariables(root.getFirstChildWithName(VARIABLES));
        Invariant invariant = parseInvariant(root.getFirstChildWithName(INVARIANT));
        ASubstitution initialisation = parseInitialisation(root.getFirstChildWithName(INITIALISATION));
        LinkedHashSet<Event> events = parseEvents(root.getFirstChildWithName(EVENTS));
        return new Machine(name, sets, assignables, invariant, initialisation, events);
    }

    public static LinkedHashSet<Predicate> parseAbstractionPredicates(File file) {
        // TODO: Create DTD for abstraction predicates
        //XMLDocument document = XMLParser.parse(file, "AbstractionPredicatesList", new File("resources/eventb/ap.dtd"), new ErrorHandler());
        XMLDocument document = XMLParser.parse(file);
        XMLNode root = document.getRoot();
        LinkedHashSet<Predicate> abstractionPredicates = new LinkedHashSet<>();
        for (int i = 0; i < root.getChildren().size(); i++) {
            abstractionPredicates.add(new Predicate("p" + i, parseBoolExpr(root.getChildren().get(i))));
        }
        return abstractionPredicates;
    }

    private static LinkedHashSet<AAssignable> parseVariables(XMLNode node) {
        return node.getChildren().stream().map(EventBParser::parseVariable).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Invariant parseInvariant(XMLNode node) {
        return new Invariant(parseBoolExpr(node.getChildren().get(0)));
    }

    private static ASubstitution parseInitialisation(XMLNode node) {
        return parseSubstitution(node.getChildren().get(0));
    }

    private static LinkedHashSet<Event> parseEvents(XMLNode node) {
        return node.getChildren().stream().map(EventBParser::parseEvent).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Event parseEvent(XMLNode node) {
        switch (node.getName()) {
            case EBMEntities.NON_GUARDED_EVENT:
                return parseNonGuardedEvent(node);
            case EBMEntities.GUARDED_EVENT:
                return parseGuardedEvent(node);
            case EBMEntities.ANY_EVENT:
                return parseAnyEvent(node);
            default:
                throw new Error("Unable to parse node \"" + node.getName() + "\": this node is not handled yet by the parser.");
        }
    }

    private static Event parseAnyEvent(XMLNode node) {
        if (!node.getAttributes().get(NAME).matches(IDENTIFIER)) {
            throw new Error("The \"" + VAL + "\" attribute for a \"" + node.getName() + "\" node must match the regular expression \"" + IDENTIFIER + "\" (\"" + node.getAttributes().get(VAL) + "\" given).");
        } else {
            return new Event(node.getAttributes().get(NAME), new Any(parseBoolExpr(node.getChildren().get(1)), parseSubstitution(node.getChildren().get(2)), parseQuantifiedVariables(node.getChildren().get(0))));
        }
    }

    private static QuantifiedVariable[] parseQuantifiedVariables(XMLNode node) {
        return node.getChildren().stream().map(EventBParser::parseQuantifiedVariable).toArray(QuantifiedVariable[]::new);
    }

    private static Event parseNonGuardedEvent(XMLNode node) {
        if (!node.getAttributes().get(NAME).matches(IDENTIFIER)) {
            throw new Error("The \"" + VAL + "\" attribute for a \"" + node.getName() + "\" node must match the regular expression \"" + IDENTIFIER + "\" (\"" + node.getAttributes().get(VAL) + "\" given).");
        } else {
            return new Event(node.getAttributes().get(NAME), parseSubstitution(node.getChildren().get(0)));
        }
    }

    private static Event parseGuardedEvent(XMLNode node) {
        if (!node.getAttributes().get(NAME).matches(IDENTIFIER)) {
            throw new Error("The \"" + VAL + "\" attribute for a \"" + node.getName() + "\" node must match the regular expression \"" + IDENTIFIER + "\" (\"" + node.getAttributes().get(VAL) + "\" given).");
        } else {
            return new Event(node.getAttributes().get(NAME), new Select(parseBoolExpr(node.getChildren().get(0)), parseSubstitution(node.getChildren().get(1))));
        }
    }

    private static ASubstitution parseSubstitution(XMLNode node) {
        switch (node.getName()) {
            case EBMEntities.SKIP:
                return parseSkip(node);
            case EBMEntities.PARALLEL:
                return parseParallel(node);
            case EBMEntities.VARIABLE_ASSIGNMENT:
                return parseVariableAssignment(node);
            case EBMEntities.MULTIPLE_ASSIGNMENT:
                return parseMultipleAssignment(node);
            case EBMEntities.SELECT:
                return parseSelect(node);
            case EBMEntities.IF:
                return parseIf(node);
            case EBMEntities.CHOICE:
                return parseChoice(node);
            case EBMEntities.ANY:
                return parseAny(node);
            default:
                throw new Error("Unable to parse node \"" + node.getName() + "\": this node is not handled yet by the parser.");
        }
    }

    private static ASubstitution parseParallel(XMLNode node) {
        return new Parallel(node.getChildren().stream().map(EventBParser::parseSubstitution).toArray(ASubstitution[]::new));
    }

    private static ASubstitution parseSkip(XMLNode node) {
        return new Skip();
    }

    private static ASubstitution parseVariableAssignment(XMLNode node) {
        return new VariableAssignment(parseVariable(node.getChildren().get(0)), parseArithExpr(node.getChildren().get(1)));
    }

    private static ASubstitution parseIf(XMLNode node) {
        if (node.getChildren().size() == 2) {
            return new IfThenElse(parseBoolExpr(node.getChildren().get(0)), parseSubstitution(node.getChildren().get(1)));
        } else if (node.getChildren().size() == 3) {
            return new IfThenElse(parseBoolExpr(node.getChildren().get(0)), parseSubstitution(node.getChildren().get(1)), parseSubstitution(node.getChildren().get(2)));
        } else {
            throw new Error("Unable to parse node \"" + node.getName() + "\": this node is not handled yet by the parser.");
        }
    }

    private static ASubstitution parseMultipleAssignment(XMLNode node) {
        return new Parallel(node.getChildren().stream().map(EventBParser::parseSubstitution).toArray(ASubstitution[]::new));
    }

    private static ASubstitution parseSelect(XMLNode node) {
        return new Select(parseBoolExpr(node.getChildren().get(0)), parseSubstitution(node.getChildren().get(1)));
    }

    private static ASubstitution parseChoice(XMLNode node) {
        return new Choice(node.getChildren().stream().map(EventBParser::parseSubstitution).toArray(ASubstitution[]::new));
    }

    private static ASubstitution parseAny(XMLNode node) {
        return new Any(parseBoolExpr(node.getChildren().get(1)), parseSubstitution(node.getChildren().get(2)), parseQuantifiedVariables(node.getChildren().get(0)));
    }

    private static AArithExpr parseArithExpr(XMLNode node) {
        switch (node.getName()) {
            case NUMBER:
                return parseNumber(node);
            case VARIABLE:
                return parseVariable(node);
            case EBMEntities.SUM:
                return parseSum(node);
            case EBMEntities.SUBTRACTION:
                return parseSubtraction(node);
            case EBMEntities.DIVISION:
                return parseDivision(node);
            default:
                throw new Error("Unable to parse node \"" + node.getName() + "\": this node is not handled yet by the parser.");
        }
    }

    private static ABoolExpr parseBoolExpr(XMLNode node) {
        switch (node.getName()) {
            case IN_DOMAIN:
                return parseInDomain(node);
            case EBMEntities.NOT:
                return parseNot(node);
            case AND:
                return parseAnd(node);
            case OR:
                return parseOr(node);
            case EQUALS:
                return parseEquals(node);
            case GREATER_THAN:
                return parseGreaterThan(node);
            default:
                throw new Error("Unable to parse node \"" + node.getName() + "\": this node is not handled yet by the parser.");
        }
    }

    private static AArithExpr parseNumber(XMLNode node) {
        try {
            return new Int(Integer.parseInt(node.getAttributes().get(VAL)));
        } catch (NumberFormatException e) {
            throw new Error("The \"" + VAL + "\" attribute for a \"" + node.getName() + "\" node must be an integer (\"" + node.getAttributes().get(VAL) + "\" given).");
        }
    }

    private static IntVariable parseVariable(XMLNode node) {
        if (!node.getAttributes().get(VAL).matches(IDENTIFIER)) {
            throw new Error("The \"" + VAL + "\" attribute for a \"" + node.getName() + "\" node must match the regular expression \"" + IDENTIFIER + "\" (\"" + node.getAttributes().get(VAL) + "\" given).");
        } else {
            return new IntVariable(node.getAttributes().get(VAL));
        }
    }

    private static QuantifiedVariable parseQuantifiedVariable(XMLNode node) {
        return new QuantifiedVariable(parseVariable(node));
    }

    private static AArithExpr parseSum(XMLNode node) {
        return new Sum(node.getChildren().stream().map(EventBParser::parseArithExpr).toArray(AArithExpr[]::new));
    }

    private static AArithExpr parseSubtraction(XMLNode node) {
        return new Subtraction(node.getChildren().stream().map(EventBParser::parseArithExpr).toArray(AArithExpr[]::new));
    }

    private static AArithExpr parseDivision(XMLNode node) {
        return new Division(node.getChildren().stream().map(EventBParser::parseArithExpr).toArray(AArithExpr[]::new));
    }

    private static ABoolExpr parseInDomain(XMLNode node) {
        switch (node.getAttributes().get(TYPE)) {
            case TYPE_NATURAL_NUMBERS:
                return new GreaterOrEqual(parseVariable(node.getChildren().get(0)), new Int(0));
            default:
                throw new Error("Unable to parse node \"" + node.getName() + "\": this node is not handled yet by the parser.");
        }
    }

    private static ABoolExpr parseNot(XMLNode node) {
        return new Not(parseBoolExpr(node.getChildren().get(0)));
    }

    private static ABoolExpr parseAnd(XMLNode node) {
        return new And(node.getChildren().stream().map(EventBParser::parseBoolExpr).toArray(ABoolExpr[]::new));
    }

    private static ABoolExpr parseOr(XMLNode node) {
        return new Or(node.getChildren().stream().map(EventBParser::parseBoolExpr).toArray(ABoolExpr[]::new));
    }

    private static ABoolExpr parseEquals(XMLNode node) {
        return new Equals(parseArithExpr(node.getChildren().get(0)), parseArithExpr(node.getChildren().get(1)));
    }

    private static ABoolExpr parseGreaterThan(XMLNode node) {
        return new GreaterThan(parseArithExpr(node.getChildren().get(0)), parseArithExpr(node.getChildren().get(1)));
    }

    /*public static Machine parseMachine(File file) {
        XMLDocument document = XMLParser.parse(file);
        XMLNode root = document.getRoot();
        if (!root.getName().equals("MACHINE")) {
            throw new Error("Unable to parse machine from file \"" + file + "\": the root node must be a \"MACHINE\" node (\"" + root.getName() + "\" given).");
        }
        if(!root.getAttributes().containsKey("name")){
            throw new Error("A \"MACHINE\" node must have a \"name\" attribute.");
        }
        if(root.getAttributes().get("name").equals("")){
            throw new Error("The \"name\" attribute of a \"MACHINE\" node cannot be empty.");
        }
        String name = root.getAttributes().get("name");
        XMLNode variablesNode = root.getFirstChildWithName("VARIABLES");
        Set<AAssignable> variables = new LinkedHashSet<>();
        if (variablesNode != null) {
            for (XMLNode child : variablesNode.getChildren()) {
                if (child.getName().equals("CVariable")) {
                    IntVariable variable = (IntVariable) parseNode(child);
                    if (!variables.add(variable)) {
                        throw new Error("Variable \"" + variable.getName() + "\" has already been declared.");
                    }
                } else {
                    throw new Error("The children nodes of the \"VARIABLES\" node must only be \"CVariable\" nodes (\"" + child.getName() + "\" given).");
                }
            }
        }
        XMLNode invariantNode = root.getFirstChildWithName("INVARIANT");
        Invariant invariant;
        if (invariantNode == null) {
            invariant = new Invariant(new True());
        } else {
            if (invariantNode.getChildren().size() != 1) {
                throw new Error("The \"INVARIANT\" node must have exactly 1 child (" + invariantNode.getChildren().size() + " given).");
            }
            XMLNode child = invariantNode.getChildren().get(0);
            if (Stream.of("CAnd", "COr", "CEquals", "CGreaterOrEqual", "CGreaterThan", "CLowerOrEqual", "CLowerThan", "CNot", "CTrue", "CFalse").noneMatch(nodeName -> child.getName().equals(nodeName))) {
                throw new Error("The children nodes of the \"INVARIANT\" node must only be \"CAnd\", \"COr\", \"CEquals\", \"CGreaterOrEqual\", \"CGreaterThan\", \"CLowerOrEqual\", \"CLowerThan\" or \"CNot\" nodes (\"" + child.getName() + "\" given).");
            }
            invariant = new Invariant((ABoolExpr) parseNode(child));
        }
        XMLNode initialisationNode = root.getFirstChildWithName("INITIALISATION");
        ASubstitution initialisation;
        if (initialisationNode == null) {
            throw new Error("Unable to parse machine from file \"" + file + "\": the \"MACHINE\" node must contain a \"INITIALISATION\" node.");
        }
        if(initialisationNode.getChildren().size() != 1){
            throw new Error("The \"INITIALISATION\" node must contain exactly one child (\"" + initialisationNode.getChildren().size() + "\" given).");
        }
        initialisation = (ASubstitution) parseNode(initialisationNode.getChildren().get(0));
        XMLNode eventsNode = root.getFirstChildWithName("EVENTS");
        LinkedHashSet<Event> events = new LinkedHashSet<>();
        if (eventsNode != null) {
            for (XMLNode child : eventsNode.getChildren()) {
                if (Stream.of("CNonGuardedEvent", "CGuardedEvent", "CAnyEvent", "CMultipleAssignment").noneMatch(nodeName -> child.getName().equals(nodeName))) {
                    throw new Error("The children nodes of the \"EVENTS\" node must only be \"CNonGuardedEvent\", \"CGuardedEvent\", \"CAnyEvent\" or \"CMultipleAssignment\" nodes (" + child.getName() + " given)");
                }
                Event event = (Event) parseNode(child);
                if (events.stream().anyMatch(declaredEvent -> declaredEvent.getName().equals(event.getName()))) {
                    throw new Error("The event \"" + event.getName() + "\" has already been declared.");
                }
                events.add(event);
            }
        }
        return new Machine(name, new LinkedHashSet<>(), variables, invariant, initialisation, events);
    }*/

    /*private static AEventBObject parseNode(XMLNode node) {
        Set<IntVariable> quantifiedVariables;
        switch (node.getName()) {
            case "CVariable":
                if (!node.getAttributes().containsKey("val")) {
                    throw new Error("A \"CVariable\" node must have a \"val\" attribute.");
                }
                if (node.getAttributes().get("val").equals("")) {
                    throw new Error("The \"val\" attribute of a \"CVariable\" node cannot be empty.");
                }
                return new IntVariable(node.getAttributes().get("val"));
            case "CNumber":
                if (!node.getAttributes().containsKey("val")) {
                    throw new Error("A \"CNumber\" node must have a \"val\" attribute.");
                }
                try {
                    return new Int(Integer.parseInt(node.getAttributes().get("val")));
                } catch (NumberFormatException e) {
                    throw new Error("Unable to parse \"CNumber\" node: the value \"" + node.getAttributes().get("val") + "\" is not an integer.");
                }
            case "CPlus":
                if (node.getChildren().size() < 2) {
                    throw new Error("A \"CPlus\" node must have at least 2 children (" + node.getChildren().size() + " given)");
                }
                return new Sum(node.getChildren().stream().map(operand -> (AArithExpr) parseNode(operand)).toArray(AArithExpr[]::new));
            case "CMinus":
                if (node.getChildren().size() < 2) {
                    throw new Error("A \"CMinus\" node must have at least 2 children (" + node.getChildren().size() + " given)");
                }
                return new Subtraction(node.getChildren().stream().map(operand -> (AArithExpr) parseNode(operand)).toArray(AArithExpr[]::new));
            case "CNot":
                if (node.getChildren().size() != 1) {
                    throw new Error("A \"CNot\" node must have exactly 1 child (" + node.getChildren().size() + " given)");
                }
                return new Not((ABoolExpr) parseNode(node.getChildren().get(0)));
            case "CAnd":
                return new And(node.getChildren().stream().map(operand -> (ABoolExpr) parseNode(operand)).toArray(ABoolExpr[]::new));
            case "COr":
                return new Or(node.getChildren().stream().map(operand -> (ABoolExpr) parseNode(operand)).toArray(ABoolExpr[]::new));
            case "CEquals":
                if (node.getChildren().size() != 2) {
                    throw new Error("The number of children for a \"CEquals\" node must be 2 (" + node.getChildren().size() + " given)");
                }
                return new Equals((AArithExpr) parseNode(node.getChildren().get(0)), (AArithExpr) parseNode(node.getChildren().get(1)));
            case "CGreater":
                if (node.getChildren().size() != 2) {
                    throw new Error("The number of children for a \"CGreater\" node must be 2 (" + node.getChildren().size() + " given)");
                }
                return new GreaterThan((AArithExpr) parseNode(node.getChildren().get(0)), (AArithExpr) parseNode(node.getChildren().get(1)));
            case "CInDomain":
                if (!node.getAttributes().containsKey("type")) {
                    throw new Error("A \"CInDomain\" node must have a \"type\" attribute.");
                }
                if (node.getChildren().size() != 1) {
                    throw new Error("A \"CInDomain\" node must have exactly 1 child (" + node.getChildren().size() + " given).");
                }
                try {
                    switch (node.getAttributes().get("type")) {
                        case "0":
                            return new GreaterOrEqual((IntVariable) parseNode(node.getChildren().get(0)), new Int(0));
                        default:
                            throw new Error("Unable to parse \"CInDomain\" node: the type \"" + node.getAttributes().get("val") + "\" is not handled yet by the parser.");
                    }
                } catch (NumberFormatException e) {
                    throw new Error("Unable to parse \"CInDomain\" node: the value \"" + node.getAttributes().get("val") + "\" is not an integer.");
                }
            case "CSkip":
                return new Skip();
            case "CAssignment":
                if (node.getChildren().size() != 2) {
                    throw new Error("The number of children for a \"CAssignment\" node must be 2 (" + node.getChildren().size() + " given)");
                }
                return new ASingleAssignment((AAssignable) parseNode(node.getChildren().get(0)), (AArithExpr) parseNode(node.getChildren().get(1)));
            case "CMultipleAssignment":
                if (node.getChildren().isEmpty()) {
                    throw new Error("A \"CMultipleAssignment\" node must have at least 1 child (none given).");
                }
                return new Parallel(node.getChildren().stream().map(assignment -> (ASingleAssignment) parseNode(assignment)).toArray(ASingleAssignment[]::new));
            case "CGuarded":
                if (node.getChildren().size() != 2) {
                    throw new Error("The number of children for a \"CGuardedEvent\" node must be 2 (" + node.getChildren().size() + " given)");
                }
                return new Select((ABoolExpr) parseNode(node.getChildren().get(0)), (ASubstitution) parseNode(node.getChildren().get(1)));
            case "CIf":
                if (node.getChildren().size() < 2 || node.getChildren().size() > 3) {
                    throw new Error("The number of children for a \"CIf\" node must be 2 or 3 (" + node.getChildren().size() + " given)");
                }
                if (node.getChildren().size() == 2) {
                    return new IfThenElse((ABoolExpr) parseNode(node.getChildren().get(0)), (ASubstitution) parseNode(node.getChildren().get(1)));
                } else {
                    return new IfThenElse((ABoolExpr) parseNode(node.getChildren().get(0)), (ASubstitution) parseNode(node.getChildren().get(1)), (ASubstitution) parseNode(node.getChildren().get(2)));
                }
            case "CNDChoice":
                if (node.getChildren().isEmpty()) {
                    throw new Error("A \"CMultipleAssignment\" node must have at least 1 child (none given).");
                }
                return new Choice(node.getChildren().stream().map(substitution -> (ASubstitution) parseNode(substitution)).toArray(ASubstitution[]::new));
            case "CAny":
                if (node.getChildren().size() != 3) {
                    throw new Error("The number of children for a \"CAny\" node must be 3 (" + node.getChildren().size() + " given)");
                }
                if (!node.getChildren().get(0).getName().equals("VariablesList")) {
                    throw new Error("The first child of a \"CAny\" node must be a \"VariablesList\" node.");
                }
                if (node.getChildren().get(0).getChildren().isEmpty()) {
                    throw new Error("A \"VariablesList\" must have at least one child.");
                }
                quantifiedVariables = new LinkedHashSet<>();
                for (XMLNode child : node.getChildren().get(0).getChildren()) {
                    if (!child.getName().equals("CVariable")) {
                        throw new Error("A \"VariablesList\" node must only have \"CVariable\" nodes as children.");
                    }
                    if (!quantifiedVariables.add((IntVariable) parseNode(child))) {
                        throw new Error("Two local variables of a \"CAny\" node cannot be defined with the same name (the name \"" + ((IntVariable) parseNode(child)).getName() + "\" was used multiple times).");
                    }
                }
                return new Any((ABoolExpr) parseNode(node.getChildren().get(1)), (ASubstitution) parseNode(node.getChildren().get(2)), quantifiedVariables.toArray(new IntVariable[quantifiedVariables.size()]));
            case "CParallel":
                if (node.getChildren().isEmpty()) {
                    throw new Error("A \"CParallel\" node must have at least one child.");
                }
                return new Parallel(node.getChildren().stream().map(substitution -> (ASubstitution) parseNode(substitution)).toArray(ASubstitution[]::new));
            case "CGuardedEvent":
                if (!node.getAttributes().containsKey("name")) {
                    throw new Error("A \"" + node.getName() + "\" node must have a \"name\" attribute.");
                }
                if (node.getAttributes().get("name").equals("")) {
                    throw new Error("The \"name\" attribute of a \"" + node.getName() + "\" node cannot be empty.");
                }
                if (node.getChildren().size() != 2) {
                    throw new Error("The number of children for a \"CGuardedEvent\" node must be 2 (" + node.getChildren().size() + " given)");
                }
                return new Event(node.getAttributes().get("name"), new Select((ABoolExpr) parseNode(node.getChildren().get(0)), (ASubstitution) parseNode(node.getChildren().get(1))));
            case "CNonGuardedEvent":
                if (!node.getAttributes().containsKey("name")) {
                    throw new Error("A \"" + node.getName() + "\" node must have a \"name\" attribute.");
                }
                if (node.getAttributes().get("name").equals("")) {
                    throw new Error("The \"name\" attribute of a \"" + node.getName() + "\" node cannot be empty.");
                }
                if (node.getChildren().size() != 1) {
                    throw new Error("The number of children for a \"CNonGuardedEvent\" node must be 1 (" + node.getChildren().size() + " given)");
                }
                return new Event(node.getAttributes().get("name"), (ASubstitution) parseNode(node.getChildren().get(0)));
            case "CAnyEvent":
                if (!node.getAttributes().containsKey("name")) {
                    throw new Error("A \"" + node.getName() + "\" node must have a \"name\" attribute.");
                }
                if (node.getAttributes().get("name").equals("")) {
                    throw new Error("The \"name\" attribute of a \"" + node.getName() + "\" node cannot be empty.");
                }
                if (node.getChildren().size() != 3) {
                    throw new Error("The number of children for a \"CAnyEvent\" node must be 3 (" + node.getChildren().size() + " given)");
                }
                if (!node.getChildren().get(0).getName().equals("VariablesList")) {
                    throw new Error("The first child of a \"CAnyEvent\" node must be a \"VariablesList\" node.");
                }
                if (node.getChildren().get(0).getChildren().isEmpty()) {
                    throw new Error("A \"VariablesList\" must have at least one child.");
                }
                quantifiedVariables = new HashSet<>();
                for (XMLNode child : node.getChildren().get(0).getChildren()) {
                    if (!child.getName().equals("CVariable")) {
                        throw new Error("A \"VariablesList\" node must only have \"CVariable\" nodes as children.");
                    }
                    if (!quantifiedVariables.add((IntVariable) parseNode(child))) {
                        throw new Error("Two local variables of a \"CAnyEvent\" node cannot be defined with the same name (the name \"" + ((IntVariable) parseNode(child)).getName() + "\" was used multiple times).");
                    }
                }
                return new Event(node.getAttributes().get("name"), new Any((ABoolExpr) parseNode(node.getChildren().get(1)), (ASubstitution) parseNode(node.getChildren().get(2)), quantifiedVariables.toArray(new IntVariable[quantifiedVariables.size()])));
            default:
                throw new Error("Unable to parse node \"" + node.getName() + "\": this node is not handled yet by the parser.");
        }
    }*/

}
