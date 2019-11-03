package mishdev.util;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Constants {

    public static final String PROGRAM_LOCATION = "src\\main\\resources\\input.txt";
    public static final String DOT_FILE_LOCATION = "src\\main\\resources\\ast.dot";
    public static final String OUTPUT_LOCATION = "src\\main\\resources\\output.svg";

    public static final String EMPTY_SYMBOL = "";
    public static final String SPACE_SYMBOL = " ";
    public static final String COMMA_SYMBOL = ",";
    public static final String EQUAL_SYMBOL = "=";
    public static final String QUOTE_SYMBOL = "\"";
    public static final String ARROW_SYMBOL = "->";
    public static final String TAB_SYMBOL = "    ";
    public static final String SEMICOLON_SYMBOL = ";";
    public static final String NEXT_STRING_SYMBOL = "\n";

    public static final String BRACKET_ROUND_OPEN = "(";
    public static final String BRACKET_ROUND_CLOSE = ")";

    public static final String BRACKET_SQUARE_OPEN = "[";
    public static final String BRACKET_SQUARE_CLOSE = "]";

    public static final String BRACKET_FIGURE_OPEN = "{";
    public static final String BRACKET_FIGURE_CLOSE = "}";

    public static final String TYPE_VOID = "void";
    public static final String IDENTIFIER_IF = "if";
    public static final String IDENTIFIER_FOR = "for";
    public static final String IDENTIFIER_ELSE = "else";
    public static final String IDENTIFIER_BREAK = "break";
    public static final String IDENTIFIER_RETURN = "return";
    public static final String IDENTIFIER_CONTINUE = "continue";

    public static final String KEYWORD_NEW = "new";
    public static final String KEYWORD_BODY = "body";
    public static final String KEYWORD_TYPE = "type";
    public static final String KEYWORD_CONST = "const";
    public static final String KEYWORD_LABEL = "label";
    public static final String KEYWORD_CYCLE = "cycle";
    public static final String KEYWORD_CLASS = "class";
    public static final String KEYWORD_FIELD = "field";
    public static final String KEYWORD_FIELDS = "fields";
    public static final String KEYWORD_METHOD = "method";
    public static final String KEYWORD_METHODS = "methods";
    public static final String KEYWORD_PACKAGE = "package";
    public static final String KEYWORD_DIGRAPH = "digraph";
    public static final String KEYWORD_VARIABLE = "variable";
    public static final String KEYWORD_OPERATOR = "operator";
    public static final String KEYWORD_MODIFIER = "modifier";
    public static final String KEYWORD_MODIFIERS = "modifiers";
    public static final String KEYWORD_CONDITION = "condition";
    public static final String KEYWORD_PARAMETER = "parameter";
    public static final String KEYWORD_THEN_BODY = "then body";
    public static final String KEYWORD_ELSE_BODY = "else body";
    public static final String KEYWORD_PARAMETERS = "parameters";
    public static final String KEYWORD_ASSIGNMENT = "assignment";
    public static final String KEYWORD_RETURN_VALUE = "return value";
    public static final String KEYWORD_DECLARE_VARIABLE = "declare variable";
    public static final String KEYWORD_CYCLE_PARAMETERS = "cycle parameters";

    public static final Set<String> UNARY_OPERATORS = ImmutableSet.of("++", "--");

    public static final Set<String> COMPARISON_OPERATORS = ImmutableSet.of(
            "==", "<", ">", "!=", "<=", ">=");

    public static final Set<String> ARITHMETIC_OPERATORS = ImmutableSet.of(
            "+", "-", "*", "/");

    public static final Set<String> MODIFIERS = ImmutableSet.of(
            "final", "static", "public", "private", "protected");

    public static final Set<String> PRIMITIVE_TYPES = ImmutableSet.of(
            "int", "char", "long", "byte", "short", "float", "double", "boolean");
}
