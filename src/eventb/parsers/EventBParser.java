package eventb.parsers;

import eventb.AEventBObject;
import eventb.Machine;
import eventb.events.*;
import eventb.exprs.arith.*;
import eventb.exprs.bool.*;
import utilities.xml.XMLDocument;
import utilities.xml.XMLNode;
import utilities.xml.XMLParser;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by gvoiron on 26/11/16.
 * Time : 16:32
 */
public final class EventBParser {

    public static Machine parseMachine(File file) {
        XMLDocument document = XMLParser.parse(file);
        XMLNode root = document.getRoot();
        if (!root.getName().equals("MACHINE")) {
            throw new Error("Unable to parse machine from file \"" + file + "\": the root node must be a \"MACHINE\" node (\"" + root.getName() + "\" found).");
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
                    throw new Error("The children nodes of the \"VARIABLES\" node must only be \"CVariable\" nodes (\"" + child.getName() + "\" found).");
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
                throw new Error("The children nodes of the \"INVARIANT\" node must only be \"CAnd\", \"COr\", \"CEquals\", \"CGreaterOrEqual\", \"CGreaterThan\", \"CLowerOrEqual\", \"CLowerThan\" or \"CNot\" nodes (\"" + child.getName() + "\" found).");
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
                    throw new Error("The children nodes of the \"EVENTS\" node must only be \"CNonGuardedEvent\", \"CGuardedEvent\", \"CAnyEvent\" or \"CMultipleAssignment\" nodes (" + child.getName() + " found)");
                }
                Event event = (Event) parseNode(child);
                if (events.stream().anyMatch(declaredEvent -> declaredEvent.getName().equals(event.getName()))) {
                    throw new Error("The event \"" + event.getName() + "\" has already been declared.");
                }
                events.add(event);
            }
        }
        return new Machine(name, new LinkedHashSet<>(), variables, invariant, initialisation, events);
    }

    private static AEventBObject parseNode(XMLNode node) {
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
                return new Assignment((AAssignable) parseNode(node.getChildren().get(0)), (AArithExpr) parseNode(node.getChildren().get(1)));
            case "CMultipleAssignment":
                if (node.getChildren().isEmpty()) {
                    throw new Error("A \"CMultipleAssignment\" node must have at least 1 child (none given).");
                }
                return new Parallel(node.getChildren().stream().map(assignment -> (Assignment) parseNode(assignment)).toArray(Assignment[]::new));
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
    }

}
