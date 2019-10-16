package mishdev.core;

import mishdev.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PreAnalyzer {

    @NotNull
    ASTNode preAnalyzePackage(@NotNull final List<String> words) {
        ASTNode packageASTNode = new ASTNode();
        packageASTNode.keyWord = words.get(0);
        packageASTNode.name = words.get(1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        return packageASTNode;
    }

    void preAnalyzeClass(@NotNull final ASTNode ASTNode,
                         @NotNull final List<String> words) {
        for (String currentWord : words) {
            if (Constants.MODIFIERS.contains(currentWord)) {
                ASTNode.modifiers.add(currentWord);
            } else if (Constants.KEYWORD_CLASS.equals(currentWord)) {
                ASTNode.keyWord = currentWord;
            } else if (!currentWord.equals(Constants.BRACKET_FIGURE_OPEN)) {
                ASTNode.name = currentWord;
            }
        }
    }

    @NotNull
    ASTNode preAnalyzeMethod(@NotNull final ASTNode classASTNode,
                             @NotNull final String classLine) {
        ASTNode methodASTNode = new ASTNode(classASTNode, Constants.KEYWORD_METHOD);
        List<String> words = Arrays
                .stream(classLine.split(Constants.SPACE_SYMBOL))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
        for (int index = 0; index < words.size(); index++) {
            String currentWord = words.get(index);
            if (currentWord.isEmpty()) {
                continue;
            }
            if (Constants.MODIFIERS.contains(currentWord)) {
                methodASTNode.modifiers.add(currentWord);
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord)
                    || Constants.TYPE_VOID.equals(currentWord)
                    || Character.isUpperCase(currentWord.charAt(0))) {
                methodASTNode.type = currentWord;
            } else if (!Constants.PRIMITIVE_TYPES.contains(currentWord)
                    && Character.isLowerCase(currentWord.charAt(0))) {
                if (currentWord.contains(Constants.BRACKET_ROUND_OPEN)) {
                    int bracketIndex = currentWord.indexOf(Constants.BRACKET_ROUND_OPEN);
                    methodASTNode.name = currentWord.substring(0, bracketIndex);
                    if (!currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                        List<ASTNode> methodParameters = analyzeMethodParameters(words
                                .stream()
                                .skip(index)
                                .collect(Collectors.toList()), methodASTNode);
                        methodASTNode.parameters.addAll(methodParameters);
                        break;
                    }
                }
            }
        }

        return methodASTNode;
    }

    @NotNull
    private List<ASTNode> analyzeMethodParameters(@NotNull final List<String> words,
                                                  @NotNull final ASTNode methodASTNode) {
        List<ASTNode> parameters = new ArrayList<>();
        ASTNode parameter = null;
        for (String currentWord : words) {
            if (parameter == null) {
                parameter = new ASTNode(methodASTNode, Constants.KEYWORD_PARAMETER);
            }
            if (currentWord.contains(Constants.BRACKET_ROUND_OPEN)) {
                int bracketIndex = currentWord.indexOf(Constants.BRACKET_ROUND_OPEN);
                String parsedWord = currentWord.substring(bracketIndex + 1);
                if (Constants.PRIMITIVE_TYPES.contains(parsedWord) || Character.isUpperCase(parsedWord.charAt(0))) {
                    parameter.type = parsedWord;
                }
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord) || Character.isUpperCase(currentWord.charAt(0))) {
                parameter.type = currentWord;
            } else if (currentWord.contains(Constants.COMMA_SYMBOL)) {
                parameter.name = currentWord.substring(0, currentWord.indexOf(Constants.COMMA_SYMBOL));
            } else if (currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                parameter.name = currentWord.substring(0, currentWord.indexOf(Constants.BRACKET_ROUND_CLOSE));
            }

            if (parameter.name != null && parameter.type != null) {
                parameters.add(parameter);
                parameter = null;
            }
        }
        return parameters;
    }
}
