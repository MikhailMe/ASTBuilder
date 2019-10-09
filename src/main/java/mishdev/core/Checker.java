package mishdev.core;

import mishdev.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Checker {

    boolean isDeclareVariable(@NotNull final List<String> words) {
        return (Constants.PRIMITIVE_TYPES.contains(words.get(0)) ||
                Character.isUpperCase(words.get(0).charAt(0)))
                && words.get(words.size() - 1).contains(Constants.SEMICOLON_SYMBOL);
    }

    boolean isStatement(@NotNull final List<String> words) {
        // first variable, equal_symbol, second variable
        return words.contains(Constants.EQUAL_SYMBOL)
                && words.get(words.size() - 1)
                .chars().mapToObj(c -> (char) c)
                .collect(Collectors.toList())
                .contains(Constants.SEMICOLON_SYMBOL.charAt(0))
                && words.size() >= 3;
    }

    boolean isCycleFor(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_FOR)
                && hasElementInCollection(words, Constants.COMPARISON_OPERATORS);
    }

    boolean isCondition(@NotNull final List<String> words) {
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
