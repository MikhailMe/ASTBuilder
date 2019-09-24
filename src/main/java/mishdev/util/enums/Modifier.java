package mishdev.util.enums;

import org.jetbrains.annotations.NotNull;

public enum Modifier {

    FINAL("final"),
    STATIC("static"),
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected");

    private String modifier;

    Modifier(@NotNull final String modifier) {
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return this.modifier;
    }
}
