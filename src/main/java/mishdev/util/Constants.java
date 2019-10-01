package mishdev.util;

import mishdev.util.enums.Modifier;
import mishdev.util.enums.PrimitiveType;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Constants {

    public static final String EMPTY_SYMBOL = "";
    public static final String SPACE_SYMBOL = " ";
    public static final String COMMA_SYMBOL = ",";
    public static final String SEMICOLON_SYMBOL = ";";
    public static final String NEXT_STRING_SYMBOL = "\n";

    public static final String BRACKET_ROUND_OPEN = "(";
    public static final String BRACKET_ROUND_CLOSE = ")";

    public static final String BRACKET_SQUARE_OPEN = "[";
    public static final String BRACKET_SQUARE_CLOSE = "]";

    public static final String BRACKET_FIGURE_OPEN = "{";
    public static final String BRACKET_FIGURE_CLOSE = "}";

    public static final String TYPE_VOID = "void";
    public static final String IDENTIFIER_RETURN = "return";

    public static final String KEYWORD_CLASS = "class";
    public static final String KEYWORD_FIELD = "field";
    public static final String KEYWORD_METHOD = "method";
    public static final String KEYWORD_PACKAGE = "package";
    public static final String KEYWORD_PARAMETER = "parameter";

    public static final Set<String> MODIFIERS = ImmutableSet.of(
            Modifier.FINAL.toString(),
            Modifier.STATIC.toString(),
            Modifier.PUBLIC.toString(),
            Modifier.PRIVATE.toString(),
            Modifier.PROTECTED.toString());

    public static final Set<String> PRIMITIVE_TYPES = ImmutableSet.of(
            PrimitiveType.INT_TYPE.toString(),
            PrimitiveType.CHAR_TYPE.toString(),
            PrimitiveType.LONG_TYPE.toString(),
            PrimitiveType.BYTE_TYPE.toString(),
            PrimitiveType.SHORT_TYPE.toString(),
            PrimitiveType.FLOAT_TYPE.toString(),
            PrimitiveType.DOUBLE_TYPE.toString(),
            PrimitiveType.BOOLEAN_TYPE.toString());
}
