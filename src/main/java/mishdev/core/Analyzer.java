package mishdev.core;

import mishdev.util.Constants;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static mishdev.util.Constants.BRACKET_FIGURE_CLOSE;
import static mishdev.util.Constants.BRACKET_FIGURE_OPEN;

public class Analyzer {

    @NotNull
    private List<String> programText;

    public Analyzer(@NotNull final List<String> programText) {
        this.programText = programText;
    }

    @NotNull
    public Node analyzePackage() {
        String[] signature = programText.get(0).split(Constants.SPACE_SYMBOL);
        Node ast = new Node();
        ast.parent = null;
        ast.modifiers = null;
        ast.keyWord = signature[0];
        ast.name = signature[1].replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        ast.children = new ArrayList<>();
        return ast;
    }

    private void preAnalyzeClass(@NotNull final Node ast,
                                 final int startClassIndex) {
        String[] signature = programText.get(startClassIndex).split(Constants.SPACE_SYMBOL);
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

    @NotNull
    public Node analyzeClass(@NotNull final Node classNode) {
        ImmutablePair<Integer, Integer> diapason = calculateDiapason(1);
        preAnalyzeClass(classNode, diapason.left);
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

    @NotNull
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

    @NotNull
    private Node preAnalyzeMethod(@NotNull final Node classNode,
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
                        break;
                    }
                }
            }
        }

        return methodNode;
    }

    @NotNull
    private List<Node> analyzeMethodParameters(final int index,
                                               @NotNull final String[] words,
                                               @NotNull final Node methodNode) {
        List<Node> parameters = new ArrayList<>();
        Node parameter = null;
        for (int i = index; i < words.length; i++) {
            String currentWord = words[i];
            if (parameter == null) {
                parameter = new Node(methodNode, null, null, null, null, Constants.KEYWORD_PARAMETER, null);
                ;
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

    @NotNull
    public Node analyzeMethod(@NotNull final Node classNode,
                              @NotNull final ImmutablePair<Integer, Integer> diapason) {
        throw new NotImplementedException("method not yet implemented");
    }

    @NotNull
    private ImmutablePair<Integer, Integer> calculateDiapason(final int startIndex) {
        if (programText.get(startIndex).contains(BRACKET_FIGURE_CLOSE)) {
            return ImmutablePair.of(startIndex, startIndex);
        }

        int endIndex = -1;
        int bracketCounter = 0;
        for (int i = startIndex + 1; i < programText.size(); i++) {
            String currentLine = programText.get(i);
            if (currentLine.contains(BRACKET_FIGURE_OPEN)) {
                bracketCounter++;
            }
            if (currentLine.contains(BRACKET_FIGURE_CLOSE)) {
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
