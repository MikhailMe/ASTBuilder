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
            } else if (checker.isStatement(lineWords)) {
                analyzeStatement(node, lineWords);
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
        Node simpleConditionNode = new Node(blockNode, Constants.IDENTIFIER_IF);
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
        } else if (checker.isStatement(words)) {
            analyzeStatement(node, words);
        } else if (checker.isSimpleCondition(words)) {
            analyzeSimpleCondition(node, words, false);
        }
    }

    // BASE METHODS

    private void analyzeSimpleCondition(@NotNull final Node conditionStatement,
                                        @NotNull final List<String> words,
                                        boolean toParameters) {
        if (toParameters) {
            Node conditionSimpleNode = new Node(conditionStatement, Constants.KEYWORD_CONDITION);
            conditionSimpleNode.name = words.get(1);

            Node left = new Node(conditionSimpleNode, Constants.LEFT_PART);
            left.name = words.get(0);

            Node right = new Node(conditionSimpleNode, Constants.RIGHT_PART);
            right.name = words.get(2);

            conditionSimpleNode.children.addAll(List.of(left, right));
            conditionStatement.parameters.add(conditionSimpleNode);
        } else {
            conditionStatement.name = words.get(1);
            conditionStatement.keyWord = Constants.KEYWORD_CONDITION;

            Node left = new Node(conditionStatement, Constants.LEFT_PART);
            left.name = words.get(0);

            Node right = new Node(conditionStatement, Constants.RIGHT_PART);
            right.name = words.get(2);

            conditionStatement.children.addAll(List.of(left, right));
        }
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

    // TODO: rewrite method
    private void analyzeStatement(@NotNull final Node statementNode,
                                  @NotNull final List<String> words) {
        statementNode.keyWord = Constants.KEYWORD_EXPRESSION;
        statementNode.name = Constants.EQUAL_SYMBOL;
        if (words.size() == 3) {
            Node leftChild = new Node(statementNode);
            leftChild.value = words.get(0);
            leftChild.keyWord = Constants.LEFT_PART;

            Node rightChild = new Node(statementNode);
            rightChild.value = helper.getValue(words);
            rightChild.keyWord = Constants.RIGHT_PART;

            statementNode.children.add(leftChild);
            statementNode.children.add(rightChild);

        } else if (words.size() == 5) {
            Node leftChild = new Node(statementNode);
            leftChild.value = words.get(0);
            leftChild.keyWord = Constants.LEFT_PART;

            Node rightChild = new Node(statementNode);
            rightChild.value = words.get(3);
            rightChild.keyWord = Constants.RIGHT_PART;

            Node leftRightChild = new Node(rightChild);
            leftRightChild.value = words.get(2);
            leftRightChild.keyWord = Constants.LEFT_PART;

            Node rightRightChild = new Node(rightChild);
            rightRightChild.value = helper.getValue(words);
            rightRightChild.keyWord = Constants.RIGHT_PART;

            rightChild.children.addAll(List.of(leftRightChild, rightRightChild));

            statementNode.children.addAll(List.of(leftChild, rightChild));
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
