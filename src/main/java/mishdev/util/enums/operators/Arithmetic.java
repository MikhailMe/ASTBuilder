package mishdev.util.enums.operators;

import org.jetbrains.annotations.NotNull;

public enum Arithmetic {

    MOD("%"),
    PLUS("+"),
    MINUS("-"),
    MULTI("*"),
    DIVISION("/");

    private String comp;

    Arithmetic(@NotNull final String comp) {
        this.comp = comp;
    }

    @Override
    public String toString() {
        return this.comp;
    }

}
