package mishdev.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import mishdev.util.enums.Modifier;
import mishdev.util.enums.PrimitiveType;

import java.util.Set;

public class Constants {

    public static final String SPACE_SYMBOL = " ";

    public static final String BRACKET_ROUND_OPEN = "(";
    public static final String BRACKET_ROUND_CLOSE = ")";

    public static final String BRACKET_SQUARE_OPEN = "[";
    public static final String BRACKET_SQUARE_CLOSE = "]";

    public static final String BRACKET_FIGURE_OPEN = "{";
    public static final String BRACKET_FIGURE_CLOSE = "}";

    public static final String IDENTIFIER_VOID = "void";
    public static final String IDENTIFIER_SEMICOLON = ";";
    public static final String IDENTIFIER_RETURN = "return";
    public static final String IDENTIFIER_CLASS = "class";
    public static final String IDENTIFIER_METHOD = "method";
    public static final String IDENTIFIER_PACKAGE = "package";

    public static final Set<String> modifiers = ImmutableSet.of(
            Modifier.FINAL.toString(),
            Modifier.STATIC.toString(),
            Modifier.PUBLIC.toString(),
            Modifier.PRIVATE.toString(),
            Modifier.PROTECTED.toString());

    public static final Set<String> primitiveTypes = ImmutableSet.of(
            PrimitiveType.INT_TYPE.toString(),
            PrimitiveType.CHAR_TYPE.toString(),
            PrimitiveType.LONG_TYPE.toString(),
            PrimitiveType.BYTE_TYPE.toString(),
            PrimitiveType.SHORT_TYPE.toString(),
            PrimitiveType.FLOAT_TYPE.toString(),
            PrimitiveType.DOUBLE_TYPE.toString(),
            PrimitiveType.BOOLEAN_TYPE.toString());
}
