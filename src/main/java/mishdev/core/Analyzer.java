package mishdev.core;

import mishdev.util.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

class Analyzer {

    @NotNull
    private Helper helper;

    @NotNull
    private Checker checker;

    @NotNull
    private PreAnalyzer preAnalyzer;

    @NotNull
    private List<String> programText;

    Analyzer(@NotNull final List<String> programText) {
        this.helper = new Helper();
        this.checker = new Checker();
        this.programText = programText;
        this.preAnalyzer = new PreAnalyzer();
    }

    @NotNull
    Node analyzeProgram() {
        return analyzePackage();
    }

    @NotNull
    private Node analyzePackage() {
        List<String> packageWords = Arrays
                .stream(programText.get(0).split(Constants.SPACE_SYMBOL))
                .collect(Collectors.toList());
        if (!checker.isPackage(packageWords)) {
            throw new InvalidParameterException("This is not package line");
        }
        Node packageNode = preAnalyzer.preAnalyzePackage(packageWords);
        Node classNode = new Node(packageNode);
        analyzeClass(classNode);
        packageNode.children.add(classNode);
        return packageNode;
    }

    private void analyzeClass(@NotNull final Node classNode) {
        int startIndex = 1;
        ImmutablePair<Integer, Integer> diapason = helper.calculateDiapason(startIndex, programText);
        List<String> classWords = Arrays
                .stream(programText.get(startIndex).split(Constants.SPACE_SYMBOL))
                .collect(Collectors.toList());
        if (!checker.isClass(classWords)) {
            throw new InvalidParameterException("This is not class line");
        }
        preAnalyzer.preAnalyzeClass(classNode, classWords);
        for (int index = diapason.left + 1; index < diapason.right; index++) {
            String classLine = programText.get(index);
            // method detect
            if (classLine.contains(Constants.BRACKET_FIGURE_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_CLOSE)) {
                Node methodNode = preAnalyzer.preAnalyzeMethod(classNode, classLine);
                ImmutablePair<Integer, Integer> methodDiapason = helper.calculateDiapason(index, programText);
                analyzeBlock(methodNode, ImmutablePair.of(methodDiapason.left + 1, methodDiapason.right));
                index = methodDiapason.right;
                classNode.children.add(methodNode);
            }
            // field detect
            else if (classLine.contains(Constants.SEMICOLON_SYMBOL)) {
                Node fieldNode = analyzeField(classNode, classLine);
                classNode.children.add(fieldNode);
            }
        }
    }

    @NotNull
    private Node analyzeField(@NotNull final Node classNode,
                              @NotNull final String classLine) {
        Node fieldNode = new Node(classNode, Constants.KEYWORD_FIELD);
        List<String> words = Arrays
                .stream(classLine.split(Constants.SPACE_SYMBOL))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
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
                fieldNode.name = currentWord.replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
            }
        }
        fieldNode.children = null;
        return fieldNode;
    }

    private void analyzeBlock(@NotNull final Node blockNode,
                              @NotNull final ImmutablePair<Integer, Integer> diapason) {
        for (int index = diapason.left; index < diapason.right; index++) {
            String methodLine = programText.get(index);
            List<String> lineWords = Arrays
                    .stream(methodLine.split(Constants.SPACE_SYMBOL))
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.toList());

            Node node = new Node(blockNode);
            if (checker.isDeclareVariable(lineWords)) {
                analyzeDeclareVariable(node, lineWords);
            } else if (checker.isExpression(lineWords)) {
                analyzeExpression(node, lineWords);
            } else if (checker.isConditionStatement(lineWords)) {
                index = analyze(index, blockNode, lineWords, this::preAnalyzeConditionStatement);
            } else if (checker.isCycleFor(lineWords)) {
                index = analyze(index, blockNode, lineWords, this::preAnalyzeCycle);
            } else if (checker.isReturn(lineWords)) {
                node.keyWord = Constants.IDENTIFIER_RETURN;
                node.value = helper.getValue(lineWords);
            }

            if (node.isUsed()) {
                blockNode.children.add(node);
            }
        }
    }

    @NotNull
    private Node preAnalyzeCycle(@NotNull final Node blockNode,
                                 @NotNull final List<String> words) {
        Node cycleNode = new Node(blockNode, Constants.KEYWORD_CYCLE);
        cycleNode.name = words.get(0);

        String cycleString = String.join(Constants.SPACE_SYMBOL, words);
        int firstIndex = cycleString.indexOf(Constants.BRACKET_ROUND_OPEN);
        int lastIndex = cycleString.lastIndexOf(Constants.BRACKET_ROUND_CLOSE);

        String cycleBody = cycleString.substring(firstIndex + 1, lastIndex);
        String[] tokens = cycleBody.split(Constants.SEMICOLON_SYMBOL);

        for (String token : tokens) {
            Node childNode = new Node(cycleNode);
            analyzeToken(childNode, Arrays
                    .stream(token.split(Constants.SPACE_SYMBOL))
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList()));
            cycleNode.parameters.add(childNode);
        }
        return cycleNode;
    }

    @NotNull
    private Node preAnalyzeConditionStatement(@NotNull final Node blockNode,
                                              @NotNull final List<String> words) {
        Node simpleConditionNode = new Node(blockNode, Constants.KEYWORD_CONDITION);
        simpleConditionNode.name = Constants.IDENTIFIER_IF;

        String conditionString = String.join(Constants.SPACE_SYMBOL, words);
        int firstIndex = conditionString.indexOf(Constants.BRACKET_ROUND_OPEN);
        int lastIndex = conditionString.lastIndexOf(Constants.BRACKET_ROUND_CLOSE);

        String condition = conditionString.substring(firstIndex + 1, lastIndex);
        analyzeSimpleCondition(simpleConditionNode, Arrays
                .stream(condition.split(Constants.SPACE_SYMBOL))
                .collect(Collectors.toList()), true);

        return simpleConditionNode;
    }

    private void analyzeToken(@NotNull final Node node,
                              @NotNull final List<String> words) {
        if (checker.isDeclareVariable(words)) {
            analyzeDeclareVariable(node, words);
        } else if (checker.isExpression(words)) {
            analyzeExpression(node, words);
        } else if (checker.isSimpleCondition(words)) {
            analyzeSimpleCondition(node, words, false);
        }
    }

    // BASE METHODS

    private void analyzeSimpleCondition(@NotNull final Node conditionStatement,
                                        @NotNull final List<String> words,
                                        boolean toParameters) {
        if (toParameters) {
            Node conditionSimpleNode = new Node(conditionStatement, Constants.KEYWORD_OPERATOR);
            getConditionChildren(conditionSimpleNode, words);
            conditionStatement.parameters.add(conditionSimpleNode);
        } else {
            conditionStatement.keyWord = Constants.KEYWORD_OPERATOR;
            getConditionChildren(conditionStatement, words);
        }
    }

    private void getConditionChildren(@NotNull final Node node,
                                      @NotNull final List<String> words) {
        node.name = words.get(1);

        Node left = new Node(node, Constants.LEFT_VAR);
        left.name = words.get(0);

        Node right = new Node(node, Constants.RIGHT_VAR);
        right.name = words.get(2).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);

        node.children.addAll(List.of(left, right));
    }

    private void analyzeDeclareVariable(@NotNull final Node declareVariableNode,
                                        @NotNull final List<String> words) {
        declareVariableNode.keyWord = Constants.KEYWORD_DECLARE_VARIABLE;
        declareVariableNode.type = words.get(0);

        if (words.get(1).contains(Constants.SEMICOLON_SYMBOL)) {
            declareVariableNode.name = words.get(1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        } else {
            declareVariableNode.name = words.get(1);
            declareVariableNode.value = helper.getValue(words);
        }
    }

    private void analyzeExpression(@NotNull final Node expressionNode,
                                   @NotNull final List<String> words) {
        if (words.contains(Constants.EQUAL_SYMBOL)) {
            int equalIndex = words.indexOf(Constants.EQUAL_SYMBOL);

            expressionNode.value = words.get(equalIndex);
            expressionNode.keyWord = Constants.KEYWORD_EXPRESSION;

            Node left = new Node(expressionNode, Constants.LEFT_VAR);
            left.value = words.get(equalIndex - 1);

            if (((words.size() - 1) - equalIndex) == 1) {
                Node right = new Node(expressionNode, Constants.RIGHT_VAR);
                right.value = words.get(equalIndex + 1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
                expressionNode.children.addAll(List.of(left, right));
            } else if (((words.size() - 1) - equalIndex) == 3) {
                List<String> rightPart = words.stream().skip(equalIndex + 1).collect(Collectors.toList());

                Node right = new Node(expressionNode, Constants.KEYWORD_OPERATOR);

                getConditionChildren(right, rightPart);

                expressionNode.children.addAll(List.of(left, right));
            }

        }
        // Assigner operations
        else {
            // INC or DEC
            if (words.size() == 1) {
                String word = words.get(0);
                String detected = word.substring(word.length() - 2);
                if (Constants.ASSIGNER_OPERATORS.contains(detected)) {
                    expressionNode.value = detected;
                    expressionNode.keyWord = Constants.KEYWORD_EXPRESSION;
                    expressionNode.name = word.substring(0, word.length() - 2);
                }
            }
        }
    }

    private int analyze(final int index,
                        @NotNull final Node blockNode,
                        @NotNull final List<String> words,
                        @NotNull BiFunction<Node, List<String>, Node> preAnalyze) {
        ImmutablePair<Integer, Integer> diapason = helper.calculateDiapason(index, programText);
        Node node = preAnalyze.apply(blockNode, words);
        analyzeBlock(node, ImmutablePair.of(diapason.left + 1, diapason.right));
        blockNode.children.add(node);
        return diapason.right;
    }
}
