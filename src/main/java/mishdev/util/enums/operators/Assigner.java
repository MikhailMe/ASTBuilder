package mishdev.util.enums.operators;

import org.jetbrains.annotations.NotNull;

public enum Assigner {

    PLUS_EQ("+="),
    MINUS_EQ("-="),
    MULTI_EQ("*="),
    DIVISION_EQ("/=");

    private String expr;

    Assigner(@NotNull final String expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return this.expr;
    }
}
