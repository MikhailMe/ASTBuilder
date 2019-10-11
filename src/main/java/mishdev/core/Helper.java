package mishdev.core;

import mishdev.util.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Helper {

    @NotNull
    ImmutablePair<Integer, Integer> calculateDiapason(final int startIndex,
                                                      @NotNull final List<String> text) {
        if (text.get(startIndex).contains(Constants.BRACKET_FIGURE_CLOSE)) {
            return ImmutablePair.of(startIndex, startIndex);
        }

        int endIndex = -1;
        int bracketCounter = 0;
        for (int i = startIndex + 1; i < text.size(); i++) {
            String currentLine = text.get(i);
            if (currentLine.contains(Constants.BRACKET_FIGURE_OPEN)) {
                bracketCounter++;
            }
            if (currentLine.contains(Constants.BRACKET_FIGURE_CLOSE)) {
                bracketCounter--;
            }
            if (bracketCounter == -1) {
                endIndex = i;
                break;
            }
        }

        if (endIndex == -1) {
            throw new IllegalArgumentException("Can't found end of class/method");
        }

        return ImmutablePair.of(startIndex, endIndex);
    }

    @NotNull
    String getValue(@NotNull final List<String> words) {
        return words.get(words.size() - 1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
    }

}
