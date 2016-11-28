package utilities;

import java.util.Collections;

/**
 * Created by gvoiron on 25/11/16.
 * Time : 15:00
 */
public abstract class AFormatter {

    private int indentation;

    public AFormatter() {
        this.indentation = 0;
    }

    public String indent() {
        return String.join("", Collections.nCopies(indentation, Chars.TABULATION));
    }

    public void indentLeft() {
        if (indentation == 0) {
            throw new Error("Unable to indent text to the left: the indentation level is already equal to 0.");
        }
        --indentation;
    }

    public void indentRight() {
        ++indentation;
    }

    private int getIndentation() {
        return indentation;
    }

}
