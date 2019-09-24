package mishdev.core.models;

import org.jetbrains.annotations.NotNull;

public enum Model {

    CYCLE("cycle"),
    METHOD("method"),
    EXPRESSION("expression");

    private String model;

    Model(@NotNull final String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return this.model;
    }

}
