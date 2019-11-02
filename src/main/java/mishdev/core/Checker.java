package mishdev.core;

import mishdev.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class Checker {

    boolean isPackage(@NotNull final List<String> words) {
        return words.contains(Constants.KEYWORD_PACKAGE);
    }

    boolean isClass(@NotNull final List<String> words) {
        return words.contains(Constants.KEYWORD_CLASS);
    }

    boolean isDeclareVariable(@NotNull final List<String> words) {
        return (Constants.PRIMITIVE_TYPES.contains(words.get(0)) ||
                Character.isUpperCase(words.get(0).charAt(0)));
    }

    boolean isSimpleCondition(@NotNull final List<String> words) {
        return hasElementInCollection(words) && words.size() == 3;
    }

    boolean isExpression(@NotNull final List<String> words) {
        boolean hasArithmeticOperators = false;
        List<String> operations = new ArrayList<>(Constants.UNARY_OPERATORS);
        for (int i = 0; i < words.size() && !hasArithmeticOperators; i++) {
            for (String operation : operations) {
                if (words.get(i).contains(operation)) {
                    hasArithmeticOperators = true;
                    break;
                }
            }
        }
        return (words.contains(Constants.EQUAL_SYMBOL) || hasArithmeticOperators)
                && !words.contains(Constants.IDENTIFIER_FOR)
                && !words.contains(Constants.IDENTIFIER_IF);
    }

    boolean isCycleFor(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_FOR)
                && hasElementInCollection(words);
    }

    boolean isConditionStatement(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_IF)
                && words.contains(Constants.BRACKET_FIGURE_OPEN);
    }

    boolean isReturn(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_RETURN);
    }

    private boolean hasElementInCollection(@NotNull final List<String> collection) {
        return collection.stream().anyMatch(Constants.COMPARISON_OPERATORS::contains);
    }

}
