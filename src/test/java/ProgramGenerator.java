import mishdev.util.Constants;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProgramGenerator {

    @NotNull
    List<String> generateProgramOnlyWithFields(int fieldAmount) {
        List<String> program = new ArrayList<>();
        program.add(generatePackageString());
        program.add(generateClassLine(generateModifier()) + Constants.BRACKET_FIGURE_OPEN);

        for (int i = 0; i < fieldAmount; i++) {
            program.add(generateFieldString(generateModifier()));
        }

        program.add(Constants.BRACKET_FIGURE_CLOSE);
        return program;
    }

    @NotNull
    List<String> generateProgramOnlyWithMethodsWithoutParameters(final int methodAmount) {
        List<String> program = new ArrayList<>();
        program.add(generatePackageString());
        program.add(generateClassLine(generateModifier()) + Constants.BRACKET_FIGURE_OPEN);

        for (int i = 0; i < methodAmount; i++) {
            program.add(generateMethodSignatureWithoutParameters(generateModifier()));
        }

        program.add(Constants.BRACKET_FIGURE_CLOSE);
        return program;
    }

    @NotNull
    List<String> generateProgramOnlyWithMethodsWithParameters(int methodAmount) {
        List<String> program = new ArrayList<>();
        program.add(generatePackageString());
        program.add(generateClassLine(generateModifier()) + Constants.BRACKET_FIGURE_OPEN);

        for (int i = 0; i < methodAmount; i++) {
            program.add(generateMethodSignatureWithParameters(generateModifier(), 3));
        }

        program.add(Constants.BRACKET_FIGURE_CLOSE);
        return program;
    }

    @NotNull
    private String generatePackageString() {
        return Constants.KEYWORD_PACKAGE +
                Constants.SPACE_SYMBOL +
                RandomStringUtils.randomAlphanumeric(5) +
                Constants.SEMICOLON_SYMBOL;
    }

    @NotNull
    private String generateClassLine(@NotNull final String modifier) {
        return modifier +
                Constants.SPACE_SYMBOL +
                Constants.KEYWORD_CLASS +
                Constants.SPACE_SYMBOL +
                RandomStringUtils.randomAlphanumeric(5) +
                Constants.SPACE_SYMBOL;
    }

    @NotNull
    private String generateFieldString(@NotNull final String modifier) {
        return modifier +
                Constants.SPACE_SYMBOL +
                generateType() +
                Constants.SPACE_SYMBOL +
                RandomStringUtils.randomAlphanumeric(5) +
                Constants.SEMICOLON_SYMBOL;
    }

    @NotNull
    private String generateParameterForMethod() {
        return generateType() + Constants.SPACE_SYMBOL + RandomStringUtils.randomAlphanumeric(5);
    }

    @NotNull
    private String generateMethodSignatureWithParameters(@NotNull final String modifier,
                                                         final int amountParameters) {
        StringBuilder methodSignatureWithParameters = new StringBuilder();
        methodSignatureWithParameters
                .append(modifier)
                .append(Constants.SPACE_SYMBOL)
                .append(generateType())
                .append(Constants.SPACE_SYMBOL)
                .append(RandomStringUtils.randomAlphanumeric(5))
                .append(Constants.BRACKET_ROUND_OPEN);

        for (int i = 0; i < amountParameters; i++) {
            methodSignatureWithParameters.append(generateParameterForMethod());
            methodSignatureWithParameters.append(Constants.COMMA_SYMBOL);
            methodSignatureWithParameters.append(Constants.SPACE_SYMBOL);
        }

        int lastSymbolIndex = methodSignatureWithParameters.length();
        methodSignatureWithParameters.delete(lastSymbolIndex - 2, lastSymbolIndex);

        methodSignatureWithParameters.append(Constants.BRACKET_ROUND_CLOSE);
        methodSignatureWithParameters.append(Constants.SPACE_SYMBOL);
        methodSignatureWithParameters.append(Constants.BRACKET_FIGURE_OPEN);
        methodSignatureWithParameters.append(Constants.BRACKET_FIGURE_CLOSE);

        return methodSignatureWithParameters.toString();
    }

    @NotNull
    private String generateMethodSignatureWithoutParameters(@NotNull final String modifier) {
        return modifier +
                Constants.SPACE_SYMBOL +
                Constants.PRIMITIVE_TYPES.toArray()[RandomUtils.nextInt(0, 8)].toString() +
                Constants.SPACE_SYMBOL +
                RandomStringUtils.randomAlphanumeric(5) +
                Constants.BRACKET_ROUND_OPEN +
                Constants.BRACKET_ROUND_CLOSE +
                Constants.SEMICOLON_SYMBOL;
    }

    @NotNull
    private String generateType() {
        return Constants.PRIMITIVE_TYPES.toArray()[RandomUtils.nextInt(0, 8)].toString();
    }

    @NotNull
    private String generateModifier() {
        return Constants.MODIFIERS.toArray()[RandomUtils.nextInt(0, 4)].toString();
    }

}
