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
        String data = words.get(1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        ASTNode packageNameNode = new ASTNode(packageASTNode, data, Constants.KEYWORD_NAME);
        packageASTNode.children.add(packageNameNode);
        return packageASTNode;
    }

    void preAnalyzeClass(@NotNull final ASTNode classNode,
                         @NotNull final List<String> words) {
        ASTNode classModifiers = new ASTNode(classNode, Constants.KEYWORD_MODIFIERS);
        for (String currentWord : words) {
            if (Constants.MODIFIERS.contains(currentWord)) {
                ASTNode modifier = new ASTNode(classModifiers, currentWord, Constants.KEYWORD_MODIFIER);
                classModifiers.children.add(modifier);
            } else if (!currentWord.equals(Constants.BRACKET_FIGURE_OPEN) && !currentWord.equals(Constants.KEYWORD_CLASS)) {
                ASTNode nameNode = new ASTNode(classModifiers, currentWord, Constants.KEYWORD_NAME);
                classNode.children.add(nameNode);
            }
        }
        classNode.children.add(classModifiers);
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
                ASTNode modifierNode = new ASTNode(methodModifiers, currentWord, Constants.KEYWORD_MODIFIER);
                methodModifiers.children.add(modifierNode);
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord)
                    || Constants.TYPE_VOID.equals(currentWord)
                    || Character.isUpperCase(currentWord.charAt(0))) {
                ASTNode typeNode = new ASTNode(methodASTNode, currentWord, Constants.KEYWORD_TYPE);
                methodASTNode.children.add(typeNode);
            } else if (!Constants.PRIMITIVE_TYPES.contains(currentWord)
                    && Character.isLowerCase(currentWord.charAt(0))) {
                if (currentWord.contains(Constants.BRACKET_ROUND_OPEN)) {
                    int bracketIndex = currentWord.indexOf(Constants.BRACKET_ROUND_OPEN);
                    String data = currentWord.substring(0, bracketIndex);
                    ASTNode nameNode = new ASTNode(methodASTNode, data, Constants.KEYWORD_NAME);
                    methodASTNode.children.add(nameNode);
                    if (!currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                        ASTNode methodParameters = analyzeMethodParameters(words
                                .stream()
                                .skip(index)
                                .collect(Collectors.toList()), methodASTNode);
                        methodASTNode.children.add(methodParameters);
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
                    ASTNode parameterType = new ASTNode(parameter, parsedWord, Constants.KEYWORD_TYPE);
                    parameter.children.add(parameterType);
                }
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord) || Character.isUpperCase(currentWord.charAt(0))) {
                ASTNode parameterType = new ASTNode(parameter, currentWord, Constants.KEYWORD_TYPE);
                parameter.children.add(parameterType);
            } else if (currentWord.contains(Constants.COMMA_SYMBOL)) {
                String data = currentWord.substring(0, currentWord.indexOf(Constants.COMMA_SYMBOL));
                ASTNode parameterName = new ASTNode(parameter, data, Constants.KEYWORD_NAME);
                parameter.children.add(parameterName);
            } else if (currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                String data = currentWord.substring(0, currentWord.indexOf(Constants.BRACKET_ROUND_CLOSE));
                ASTNode parameterName = new ASTNode(parameter, data, Constants.KEYWORD_NAME);
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
