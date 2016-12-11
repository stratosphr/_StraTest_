package eventb.substitutions;

import eventb.visitors.EventBFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 16:34
 */
public final class Choice extends ASubstitution {

    private final List<ASubstitution> substitutions;

    public Choice(ASubstitution... substitutions) {
        if (substitutions.length == 0) {
            throw new Error("A \"Choice\" substitution requires at least one substitution (none given).");
        }
        this.substitutions = Arrays.asList(substitutions);
    }

    @Override
    public String accept(EventBFormatter visitor) {
        return visitor.visit(this);
    }

    public List<ASubstitution> getSubstitutions() {
        return substitutions;
    }

}
