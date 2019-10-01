package mishdev.core;

import mishdev.util.Constants;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Analyzer extends Detector {

    public Analyzer(@NotNull final List<String> programText) {
        super(programText);
        this.programText = programText;
    }

    public Node analyzePackage(final int packageIndex) {
        String[] signature = programText.get(packageIndex).split(Constants.SPACE_SYMBOL);
        Node ast = new Node();
        ast.parent = null;
        ast.modifiers = null;
        ast.keyWord = signature[0];
        ast.name = signature[1].replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        ast.children = new ArrayList<>();
        return ast;
    }

    private void preAnalyzeClass(@NotNull final Node ast,
                                 @NotNull final ImmutablePair<Integer, Integer> diapason) {
        String[] signature = programText.get(diapason.left).split(Constants.SPACE_SYMBOL);
        for (String currentWord : signature) {
            if (Constants.MODIFIERS.contains(currentWord)) {
                ast.modifiers.add(currentWord);
            } else if (Constants.KEYWORD_CLASS.equals(currentWord)) {
                ast.keyWord = currentWord;
            } else if (!currentWord.equals(Constants.BRACKET_FIGURE_OPEN)) {
                ast.name = currentWord;
            }
        }
        ast.children = new ArrayList<>();
    }

    public Node analyzeClass(@NotNull final Node classNode,
                             @NotNull final ImmutablePair<Integer, Integer> diapason) {
        preAnalyzeClass(classNode, diapason);
        for (int index = diapason.left + 1; index < diapason.right; index++) {
            String classLine = programText.get(index);
            // method detect
            if (classLine.contains(Constants.BRACKET_FIGURE_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_CLOSE)) {
                Node methodNode = preAnalyzeMethod(classNode, classLine);
                ImmutablePair<Integer, Integer> methodDiapason = calculateDiapason(index);
                //analyzeMethod(methodNode, ImmutablePair.of(methodDiapason.left + 1, methodDiapason.right));
                index = methodDiapason.right;
                classNode.children.add(methodNode);
            }
            // field detect
            else if (classLine.contains(Constants.SEMICOLON_SYMBOL)) {
                Node fieldNode = analyzeField(classNode, classLine);
                classNode.children.add(fieldNode);
            }
        }
        return classNode;
    }

    private Node analyzeField(@NotNull final Node classNode,
                              @NotNull final String classLine) {
        Node fieldNode = new Node(classNode);
        fieldNode.keyWord = Constants.KEYWORD_FIELD;
        String[] words = classLine.split(Constants.SPACE_SYMBOL);
        for (String currentWord : words) {
            if (currentWord.isEmpty()) {
                continue;
            }
            if (Constants.MODIFIERS.contains(currentWord)) {
                fieldNode.modifiers.add(currentWord);
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord)
                    || Constants.TYPE_VOID.equals(currentWord)
                    || Character.isUpperCase(currentWord.charAt(0))) {
                fieldNode.type = currentWord;
            } else if (!Constants.PRIMITIVE_TYPES.contains(currentWord)
                    && Character.isLowerCase(currentWord.charAt(0))) {
                fieldNode.name = currentWord.replace(Constants.SEMICOLON_SYMBOL, "");
            }
        }
        fieldNode.children = null;
        return fieldNode;
    }

    public Node preAnalyzeMethod(@NotNull final Node classNode,
                                 @NotNull final String classLine) {
        Node methodNode = new Node(classNode);
        methodNode.keyWord = Constants.KEYWORD_METHOD;
        String[] words = classLine.split(Constants.SPACE_SYMBOL);
        for (int index = 0; index < words.length; index++) {
            String currentWord = words[index];
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
                        methodNode.parameters = analyzeMethodParameters(index, words, methodNode);
                    }
                }
            }
        }

        return methodNode;
    }

    public List<Node> analyzeMethodParameters(final int index,
                                              @NotNull final String[] words,
                                              @NotNull final Node methodNode) {
        List<Node> parameters = new ArrayList<>();
        Node parameter = createParameter(methodNode);
        for (int i = index; i < words.length; i++) {
            String currentWord = words[i];
            if (parameter == null) {
                parameter = createParameter(methodNode);
            }
            if (currentWord.contains(Constants.BRACKET_ROUND_OPEN)) {
                int bracketIndex = currentWord.indexOf(Constants.BRACKET_ROUND_OPEN);
                parameter.type = currentWord.substring(bracketIndex + 1);
            } else if (currentWord.contains(Constants.BRACKET_ROUND_CLOSE)) {
                int bracketIndex = currentWord.indexOf(Constants.BRACKET_ROUND_CLOSE);
                parameter.name = currentWord.substring(0, bracketIndex);
                if (parameter.type != null) {
                    parameters.add(parameter);
                    parameter = null;
                }
            }
            // TODO: 10/1/2019 add support many parameters (example: commas)
            // TODO: 10/1/2019 also add support modifiers for parameters (example: final)
        }
        return parameters;
    }

    private Node createParameter(Node parent) {
        Node parameter = new Node(parent);
        parameter.children = null;
        parameter.modifiers = null;
        parameter.parameters = null;
        parameter.keyWord = Constants.KEYWORD_PARAMETER;
        return parameter;
    }

    public Node analyzeMethod(@NotNull final Node classNode,
                              @NotNull final ImmutablePair<Integer, Integer> diapason) {
        throw new NotImplementedException("method not yet implemented");
    }

}
