package mishdev.core;

import mishdev.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PreAnalyzer {

    @NotNull
    Node preAnalyzePackage(@NotNull final List<String> words) {
        Node packageNode = new Node();
        packageNode.keyWord = words.get(0);
        packageNode.name = words.get(1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        return packageNode;
    }

    void preAnalyzeClass(@NotNull final Node node,
                         @NotNull final List<String> words) {
        for (String currentWord : words) {
            if (Constants.MODIFIERS.contains(currentWord)) {
                node.modifiers.add(currentWord);
            } else if (Constants.KEYWORD_CLASS.equals(currentWord)) {
                node.keyWord = currentWord;
            } else if (!currentWord.equals(Constants.BRACKET_FIGURE_OPEN)) {
                node.name = currentWord;
            }
        }
    }

    @NotNull
    Node preAnalyzeMethod(@NotNull final Node classNode,
                          @NotNull final String classLine) {
        Node methodNode = new Node(classNode, Constants.KEYWORD_METHOD);
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
                methodNode.modifiers.add(currentWord);
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord)
                    || Constants.TYPE_VOID.equals(currentWord)
                    || Character.isUpperCase(currentWord.charAt(0))) {
                methodNode.type = currentWord;
            } else if (!Constants.PRIMITIVE_TYPES.contains(currentWord)
                    && Character.isLowerCase(currentWord.charAt(0))) {
                if (currentWord.contains(Constants.BRACKET_ROUND_OPEN)) {
                    int bracketIndex = currentWord.indexOf(Constants.BRACKET_ROUND_OPEN);
                    methodNode.name = currentWord.substring(0, bracketIndex);
                    if (!currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                        List<Node> methodParameters = analyzeMethodParameters(words
                                .stream()
                                .skip(index)
                                .collect(Collectors.toList()), methodNode);
                        methodNode.parameters.addAll(methodParameters);
                        break;
                    }
                }
            }
        }

        return methodNode;
    }

    @NotNull
    private List<Node> analyzeMethodParameters(@NotNull final List<String> words,
                                               @NotNull final Node methodNode) {
        List<Node> parameters = new ArrayList<>();
        Node parameter = null;
        for (String currentWord : words) {
            if (parameter == null) {
                parameter = new Node(methodNode, Constants.KEYWORD_PARAMETER);
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
