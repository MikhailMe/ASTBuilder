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
        ASTNode methodModifiers = new ASTNode(methodASTNode, Constants.KEYWORD_MODIFIERS);
        for (int index = 0; index < words.size(); index++) {
            String currentWord = words.get(index);
            if (currentWord.isEmpty()) {
                continue;
            }
            if (Constants.MODIFIERS.contains(currentWord)) {
                ASTNode modifierNode = new ASTNode(methodModifiers, Constants.KEYWORD_MODIFIER);
                modifierNode.name = currentWord;
                methodModifiers.children.add(modifierNode);
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord)
                    || Constants.TYPE_VOID.equals(currentWord)
                    || Character.isUpperCase(currentWord.charAt(0))) {
                ASTNode typeNode = new ASTNode(methodASTNode, Constants.KEYWORD_TYPE);
                typeNode.name = currentWord;
                methodASTNode.children.add(typeNode);
            } else if (!Constants.PRIMITIVE_TYPES.contains(currentWord)
                    && Character.isLowerCase(currentWord.charAt(0))) {
                if (currentWord.contains(Constants.BRACKET_ROUND_OPEN)) {
                    int bracketIndex = currentWord.indexOf(Constants.BRACKET_ROUND_OPEN);
                    ASTNode nameNode = new ASTNode(methodASTNode, Constants.KEYWORD_NAME);
                    nameNode.name = currentWord.substring(0, bracketIndex);
                    methodASTNode.children.add(nameNode);
                    if (!currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                        ASTNode methodParameters = analyzeMethodParameters(words
                                .stream()
                                .skip(index)
                                .collect(Collectors.toList()), methodASTNode);
                        methodASTNode.parameters.add(methodParameters);
                        break;
                    }
                }
            }
        }
        methodASTNode.children.add(methodModifiers);
        return methodASTNode;
    }

    @NotNull
    private ASTNode analyzeMethodParameters(@NotNull final List<String> words,
                                            @NotNull final ASTNode methodASTNode) {
        ASTNode parametersNode = new ASTNode(methodASTNode, Constants.KEYWORD_PARAMETERS);

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
                    ASTNode parameterType = new ASTNode(parameter, Constants.KEYWORD_TYPE);
                    parameterType.type = parsedWord;
                    parameter.children.add(parameterType);
                }
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord) || Character.isUpperCase(currentWord.charAt(0))) {
                ASTNode parameterType = new ASTNode(parameter, Constants.KEYWORD_TYPE);
                parameterType.type = currentWord;
                parameter.children.add(parameterType);
            } else if (currentWord.contains(Constants.COMMA_SYMBOL)) {
                ASTNode parameterName = new ASTNode(parameter, Constants.KEYWORD_NAME);
                parameterName.type = currentWord.substring(0, currentWord.indexOf(Constants.COMMA_SYMBOL));
                parameter.children.add(parameterName);
            } else if (currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                ASTNode parameterName = new ASTNode(parameter, Constants.KEYWORD_NAME);
                parameterName.type = currentWord.substring(0, currentWord.indexOf(Constants.BRACKET_ROUND_CLOSE));
                parameter.children.add(parameterName);
            }

            if (parameter.children != null && parameter.children.size() == 2) {
                parameters.add(parameter);
                parameter = null;
            }
        }

        parametersNode.children.addAll(parameters);

        return parametersNode;
    }
}
