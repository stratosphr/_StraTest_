package graphs;

/**
 * Created by gvoiron on 09/12/16.
 * Time : 14:58
 */
public abstract class ALabelledTransition<State, Label> {

    private final State source;
    private final Label label;
    private final State target;

    public ALabelledTransition(State source, Label label, State target) {
        this.source = source;
        this.label = label;
        this.target = target;
    }

    public State getSource() {
        return source;
    }

    public Label getLabel() {
        return label;
    }

    public State getTarget() {
        return target;
    }

    @Override
    public int hashCode() {
        return (getClass().getName() + getSource() + getLabel() + getTarget()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ALabelledTransition && getSource().equals(((ALabelledTransition) o).getSource()) && getLabel().equals(((ALabelledTransition) o).getLabel()) && getTarget().equals(((ALabelledTransition) o).getTarget());
    }

    @Override
    public String toString() {
        return getSource() + " -[ " + getLabel() + " ]-> " + getTarget();
    }

}
