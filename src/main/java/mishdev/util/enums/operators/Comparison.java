package mishdev.util.enums.operators;

import org.jetbrains.annotations.NotNull;

public enum Comparison {

    EQ("=="),
    LESS("<"),
    MORE(">"),
    NOT_EQ("!="),
    LESS_EQ("<="),
    MORE_Q(">=");

    private String comp;

    Comparison(@NotNull final String comp) {
        this.comp = comp;
    }

    @Override
    public String toString() {
        return this.comp;
    }
}
