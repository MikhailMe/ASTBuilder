package mishdev.core;

import mishdev.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

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
        return hasElementInCollection(words, Constants.COMPARISON_OPERATORS) && words.size() == 3;
    }

    // TODO: need support increment ++
    boolean isStatement(@NotNull final List<String> words) {
        return words.contains(Constants.EQUAL_SYMBOL)
                && !words.contains(Constants.IDENTIFIER_FOR)
                && !words.contains(Constants.IDENTIFIER_IF)
                && words.size() >= 3;
    }

    boolean isCycleFor(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_FOR)
                && hasElementInCollection(words, Constants.COMPARISON_OPERATORS);
    }

    boolean isConditionStatement(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_IF)
                && words.contains(Constants.BRACKET_FIGURE_OPEN);
    }

    boolean isReturn(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_RETURN);
    }

    private boolean hasElementInCollection(@NotNull final List<String> collection,
                                           @NotNull final Set<String> checkSet) {
        return collection.stream().anyMatch(checkSet::contains);
    }

}
