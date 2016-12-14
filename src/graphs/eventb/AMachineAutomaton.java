package graphs.eventb;

import eventb.Event;
import graphs.AFiniteStateAutomaton;

import java.util.Set;

/**
 * Created by gvoiron on 13/12/16.
 * Time : 13:03
 */
public abstract class AMachineAutomaton<State extends AState, Transition extends ATransition<State>> extends AFiniteStateAutomaton<State, Event, Transition> {

    public AMachineAutomaton(Set<State> states, Set<State> initialStates, Set<State> finalStates, Set<Transition> transitions) {
        super(states, initialStates, finalStates, transitions);
    }

}
