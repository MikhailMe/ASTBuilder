package mishdev.util.enums;

import org.jetbrains.annotations.NotNull;

public enum PrimitiveType {

    INT_TYPE("int"),
    CHAR_TYPE("char"),
    LONG_TYPE("long"),
    BYTE_TYPE("byte"),
    SHORT_TYPE("short"),
    FLOAT_TYPE("float"),
    DOUBLE_TYPE("double"),
    BOOLEAN_TYPE("boolean");

    private String type;

    PrimitiveType(@NotNull final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }

}
