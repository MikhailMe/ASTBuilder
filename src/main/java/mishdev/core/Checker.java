package mishdev.core;

import mishdev.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Checker {

    public boolean isStatement(@NotNull final List<String> words) {
        return (Constants.PRIMITIVE_TYPES.contains(words.get(0)) ||
                Character.isUpperCase(words.get(0).charAt(0)))
                && words.get(words.size() - 1).contains(Constants.SEMICOLON_SYMBOL);
    }

    public boolean isCycleFor(@NotNull final List<String> words) {
        return false;
    }

    public boolean isCycleWhile(@NotNull final List<String> words) {
        return false;
    }

    public boolean isCondition(@NotNull final List<String> words) {
        return false;
    }

    public boolean isReturn(@NotNull final List<String> words) {
        return words.contains(Constants.IDENTIFIER_RETURN);
    }
}
