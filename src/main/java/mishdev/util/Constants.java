package mishdev.util;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Constants {

    public static final String EMPTY_SYMBOL = "";
    public static final String SPACE_SYMBOL = " ";
    public static final String COMMA_SYMBOL = ",";
    public static final String EQUAL_SYMBOL = "=";
    public static final String SEMICOLON_SYMBOL = ";";
    public static final String NEXT_STRING_SYMBOL = "\n";

    public static final String BRACKET_ROUND_OPEN = "(";
    public static final String BRACKET_ROUND_CLOSE = ")";

    public static final String BRACKET_SQUARE_OPEN = "[";
    public static final String BRACKET_SQUARE_CLOSE = "]";

    public static final String BRACKET_FIGURE_OPEN = "{";
    public static final String BRACKET_FIGURE_CLOSE = "}";

    public static final String LEFT_PART = "left";
    public static final String RIGHT_PART = "right";

    public static final String TYPE_VOID = "void";
    public static final String IDENTIFIER_IF = "if";
    public static final String IDENTIFIER_FOR = "for";
    public static final String IDENTIFIER_ELSE = "else";
    public static final String IDENTIFIER_RETURN = "return";

    public static final String KEYWORD_CYCLE = "cycle";
    public static final String KEYWORD_CLASS = "class";
    public static final String KEYWORD_FIELD = "field";
    public static final String KEYWORD_METHOD = "method";
    public static final String KEYWORD_PACKAGE = "package";
    public static final String KEYWORD_COMMAND = "command";
    public static final String KEYWORD_VARIABLE = "variable";
    public static final String KEYWORD_CONDITION = "condition";
    public static final String KEYWORD_PARAMETER = "parameter";
    public static final String KEYWORD_STATEMENT = "statement";
    public static final String KEYWORD_EXPRESSION = "expression";
    public static final String KEYWORD_DECLARE_VARIABLE = "declare variable";

    public static final Set<String> ASSIGNER_OPERATORS = ImmutableSet.of(
            "++", "--", "+=", "-=", "*=", "/=");

    public static final Set<String> COMPARISON_OPERATORS = ImmutableSet.of(
            "==", "<", ">", "!=", "<=", ">=");

    public static final Set<String> MODIFIERS = ImmutableSet.of(
            "final", "static", "public", "private", "protected");

    public static final Set<String> PRIMITIVE_TYPES = ImmutableSet.of(
            "int", "char", "long", "byte", "short", "float", "double", "boolean");
}
