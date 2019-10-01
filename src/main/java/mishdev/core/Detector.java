package mishdev.core;

import mishdev.core.models.StructuredProgram;
import mishdev.util.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static mishdev.util.Constants.*;

public class Detector {

    @NotNull
    List<String> programText;

    @NotNull
    StructuredProgram structuredProgram;

    public Detector(@NotNull final List<String> programText) {
        this.programText = programText;
        this.structuredProgram = new StructuredProgram();
    }

    // TODO: 10/1/2019 get rid this method
    StructuredProgram detect() {
        for (int i = 0; i < programText.size(); i++) {
            String currentLine = programText.get(i);
            // detect package
            if (currentLine.contains(KEYWORD_PACKAGE)) {
                structuredProgram.filePackage = i;
            }
            // detect strings with modifiers
            else if (Arrays.stream(currentLine.split(SPACE_SYMBOL)).anyMatch(Constants.MODIFIERS::contains)) {
                // detect class
                if (currentLine.contains(KEYWORD_CLASS) && currentLine.contains(BRACKET_FIGURE_OPEN)) {
                    structuredProgram.fileClass = calculateDiapason(i);
                }
                // detect fields
                else if (currentLine.contains(SEMICOLON_SYMBOL)) {
                    structuredProgram.fields.add(i);
                }
                // detect methods
                else if (currentLine.contains(BRACKET_ROUND_OPEN)
                        && currentLine.contains(BRACKET_ROUND_CLOSE)
                        && currentLine.contains(BRACKET_FIGURE_OPEN)) {
                    structuredProgram.methods.add(calculateDiapason(i));
                }
            }
        }
        return this.structuredProgram;
    }

    @NotNull
    ImmutablePair<Integer, Integer> calculateDiapason(final int startIndex) {
        int endIndex = -1;
        int bracketCounter = 0;
        for (int i = startIndex + 1; i < programText.size(); i++) {
           String currentLine = programText.get(i);
           if (currentLine.contains(BRACKET_FIGURE_OPEN)) {
               bracketCounter++;
           } else if (currentLine.contains(BRACKET_FIGURE_CLOSE)) {
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
}
