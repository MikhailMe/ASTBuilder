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
    private Checker checker;

    @NotNull
    private PreAnalyzer preAnalyzer;

    @NotNull
    private List<String> programText;

    Analyzer(@NotNull final List<String> programText) {
        this.checker = new Checker();
        this.programText = programText;
        this.preAnalyzer = new PreAnalyzer();
    }

    @NotNull
    ASTNode analyzeProgram() {
        return analyzePackage();
    }

    @NotNull
    private ASTNode analyzePackage() {
        List<String> packageWords = Arrays
                .stream(programText.get(0).split(Constants.SPACE_SYMBOL))
                .collect(Collectors.toList());
        if (!checker.isPackage(packageWords)) {
            throw new InvalidParameterException("This is not package line");
        }
        ASTNode packageASTNode = preAnalyzer.preAnalyzePackage(packageWords);
        ASTNode classASTNode = new ASTNode(packageASTNode);
        analyzeClass(classASTNode);
        packageASTNode.children.add(classASTNode);
        return packageASTNode;
    }

    private void analyzeClass(@NotNull final ASTNode classASTNode) {
        int startIndex = 1;
        ImmutablePair<Integer, Integer> diapason = this.calculateDiapason(startIndex, programText);
        List<String> classWords = Arrays
                .stream(programText.get(startIndex).split(Constants.SPACE_SYMBOL))
                .collect(Collectors.toList());
        if (!checker.isClass(classWords)) {
            throw new InvalidParameterException("This is not class line");
        }
        preAnalyzer.preAnalyzeClass(classASTNode, classWords);
        for (int index = diapason.left + 1; index < diapason.right; index++) {
            String classLine = programText.get(index);
            // method detect
            if (classLine.contains(Constants.BRACKET_FIGURE_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_CLOSE)) {
                ASTNode methodASTNode = preAnalyzer.preAnalyzeMethod(classASTNode, classLine);
                ImmutablePair<Integer, Integer> methodDiapason = this.calculateDiapason(index, programText);
                analyzeBlock(methodASTNode, ImmutablePair.of(methodDiapason.left + 1, methodDiapason.right));
                index = methodDiapason.right;
                classASTNode.children.add(methodASTNode);
            }
            // field detect
            else if (classLine.contains(Constants.SEMICOLON_SYMBOL)) {
                ASTNode fieldASTNode = analyzeField(classASTNode, classLine);
                classASTNode.children.add(fieldASTNode);
            }
        }
    }

    @NotNull
    private ASTNode analyzeField(@NotNull final ASTNode classASTNode,
                                 @NotNull final String classLine) {
        ASTNode fieldASTNode = new ASTNode(classASTNode, Constants.KEYWORD_FIELD);
        List<String> words = Arrays
                .stream(classLine.split(Constants.SPACE_SYMBOL))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
        for (String currentWord : words) {
            if (currentWord.isEmpty()) {
                continue;
            }
            if (Constants.MODIFIERS.contains(currentWord)) {
                fieldASTNode.modifiers.add(currentWord);
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord)
                    || Constants.TYPE_VOID.equals(currentWord)
                    || Character.isUpperCase(currentWord.charAt(0))) {
                fieldASTNode.type = currentWord;
            } else if (!Constants.PRIMITIVE_TYPES.contains(currentWord)
                    && Character.isLowerCase(currentWord.charAt(0))) {
                fieldASTNode.name = currentWord.replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
            }
        }
        fieldASTNode.children = null;
        return fieldASTNode;
    }

    private void analyzeBlock(@NotNull final ASTNode blockASTNode,
                              @NotNull final ImmutablePair<Integer, Integer> diapason) {
        for (int index = diapason.left; index < diapason.right; index++) {
            String methodLine = programText.get(index);
            List<String> lineWords = Arrays
                    .stream(methodLine.split(Constants.SPACE_SYMBOL))
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.toList());

            ASTNode node = new ASTNode(blockASTNode);
            if (checker.isDeclareVariable(lineWords)) {
                analyzeDeclareVariable(node, lineWords);
            } else if (checker.isExpression(lineWords)) {
                analyzeExpression(node, lineWords);
            } else if (checker.isConditionStatement(lineWords)) {
                index = analyze(index, blockASTNode, lineWords, this::preAnalyzeConditionStatement);
            } else if (checker.isCycleFor(lineWords)) {
                index = analyze(index, blockASTNode, lineWords, this::preAnalyzeCycle);
            } else if (checker.isReturn(lineWords)) {
                node.keyWord = Constants.IDENTIFIER_RETURN;
                node.value = this.getValue(lineWords);
            }

            if (node.isUsed()) {
                blockASTNode.children.add(node);
            }
        }
    }

    @NotNull
    private ASTNode preAnalyzeCycle(@NotNull final ASTNode blockASTNode,
                                    @NotNull final List<String> words) {
        ASTNode cycleASTNode = new ASTNode(blockASTNode, Constants.KEYWORD_CYCLE);
        cycleASTNode.name = words.get(0);

        String cycleString = String.join(Constants.SPACE_SYMBOL, words);
        int firstIndex = cycleString.indexOf(Constants.BRACKET_ROUND_OPEN);
        int lastIndex = cycleString.lastIndexOf(Constants.BRACKET_ROUND_CLOSE);

        String cycleBody = cycleString.substring(firstIndex + 1, lastIndex);
        String[] tokens = cycleBody.split(Constants.SEMICOLON_SYMBOL);

        for (String token : tokens) {
            ASTNode childASTNode = new ASTNode(cycleASTNode);
            analyzeToken(childASTNode, Arrays
                    .stream(token.split(Constants.SPACE_SYMBOL))
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList()));
            cycleASTNode.parameters.add(childASTNode);
        }
        return cycleASTNode;
    }

    @NotNull
    private ASTNode preAnalyzeConditionStatement(@NotNull final ASTNode blockASTNode,
                                                 @NotNull final List<String> words) {
        ASTNode simpleConditionASTNode = new ASTNode(blockASTNode, Constants.KEYWORD_CONDITION);
        simpleConditionASTNode.name = Constants.IDENTIFIER_IF;

        String conditionString = String.join(Constants.SPACE_SYMBOL, words);
        int firstIndex = conditionString.indexOf(Constants.BRACKET_ROUND_OPEN);
        int lastIndex = conditionString.lastIndexOf(Constants.BRACKET_ROUND_CLOSE);

        String condition = conditionString.substring(firstIndex + 1, lastIndex);
        analyzeSimpleCondition(simpleConditionASTNode, Arrays
                .stream(condition.split(Constants.SPACE_SYMBOL))
                .collect(Collectors.toList()), true);

        return simpleConditionASTNode;
    }

    private void analyzeToken(@NotNull final ASTNode ASTNode,
                              @NotNull final List<String> words) {
        if (checker.isDeclareVariable(words)) {
            analyzeDeclareVariable(ASTNode, words);
        } else if (checker.isExpression(words)) {
            analyzeExpression(ASTNode, words);
        } else if (checker.isSimpleCondition(words)) {
            analyzeSimpleCondition(ASTNode, words, false);
        }
    }

    // BASE METHODS

    private void analyzeSimpleCondition(@NotNull final ASTNode conditionStatement,
                                        @NotNull final List<String> words,
                                        boolean toParameters) {
        if (toParameters) {
            ASTNode conditionSimpleASTNode = new ASTNode(conditionStatement, Constants.KEYWORD_OPERATOR);
            getConditionChildren(conditionSimpleASTNode, words);
            conditionStatement.parameters.add(conditionSimpleASTNode);
        } else {
            conditionStatement.keyWord = Constants.KEYWORD_OPERATOR;
            getConditionChildren(conditionStatement, words);
        }
    }

    private void getConditionChildren(@NotNull final ASTNode node,
                                      @NotNull final List<String> words) {
        node.value = words.get(1);

        ASTNode left = new ASTNode(node, Constants.LEFT_VAR);
        left.value = words.get(0);

        ASTNode right = new ASTNode(node, Constants.RIGHT_VAR);
        right.value = words.get(2).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);

        node.children.addAll(List.of(left, right));
    }

    private void analyzeDeclareVariable(@NotNull final ASTNode declareVariableASTNode,
                                        @NotNull final List<String> words) {
        declareVariableASTNode.keyWord = Constants.KEYWORD_DECLARE_VARIABLE;
        declareVariableASTNode.type = words.get(0);

        if (words.get(1).contains(Constants.SEMICOLON_SYMBOL)) {
            declareVariableASTNode.name = words.get(1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        } else {
            declareVariableASTNode.name = words.get(1);
            declareVariableASTNode.value = this.getValue(words);
        }
    }

    private void analyzeExpression(@NotNull final ASTNode expressionASTNode,
                                   @NotNull final List<String> words) {
        if (words.contains(Constants.EQUAL_SYMBOL)) {
            int equalIndex = words.indexOf(Constants.EQUAL_SYMBOL);

            expressionASTNode.value = words.get(equalIndex);
            expressionASTNode.keyWord = Constants.KEYWORD_EXPRESSION;

            ASTNode left = new ASTNode(expressionASTNode, Constants.LEFT_VAR);
            left.value = words.get(equalIndex - 1);

            if (((words.size() - 1) - equalIndex) == 1) {
                ASTNode right = new ASTNode(expressionASTNode, Constants.RIGHT_VAR);
                right.value = words.get(equalIndex + 1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
                expressionASTNode.children.addAll(List.of(left, right));
            } else if (((words.size() - 1) - equalIndex) == 3) {
                List<String> rightPart = words.stream().skip(equalIndex + 1).collect(Collectors.toList());

                ASTNode right = new ASTNode(expressionASTNode, Constants.KEYWORD_OPERATOR);

                getConditionChildren(right, rightPart);

                expressionASTNode.children.addAll(List.of(left, right));
            }

        }
        // Assigner operations
        else {
            // INC or DEC
            if (words.size() == 1) {
                String word = words.get(0);
                String detected = word.substring(word.length() - 2);
                if (Constants.ASSIGNER_OPERATORS.contains(detected)) {
                    expressionASTNode.value = detected;
                    expressionASTNode.keyWord = Constants.KEYWORD_EXPRESSION;
                    expressionASTNode.name = word.substring(0, word.length() - 2);
                }
            }
        }
    }

    private int analyze(final int index,
                        @NotNull final ASTNode blockASTNode,
                        @NotNull final List<String> words,
                        @NotNull BiFunction<ASTNode, List<String>, ASTNode> preAnalyze) {
        ImmutablePair<Integer, Integer> diapason = this.calculateDiapason(index, programText);
        ASTNode node = preAnalyze.apply(blockASTNode, words);
        analyzeBlock(node, ImmutablePair.of(diapason.left + 1, diapason.right));
        blockASTNode.children.add(node);
        return diapason.right;
    }

    @NotNull
    private ImmutablePair<Integer, Integer> calculateDiapason(final int startIndex,
                                                              @NotNull final List<String> text) {
        if (text.get(startIndex).contains(Constants.BRACKET_FIGURE_CLOSE)) {
            return ImmutablePair.of(startIndex, startIndex);
        }

        int endIndex = -1;
        int bracketCounter = 0;
        for (int i = startIndex + 1; i < text.size(); i++) {
            String currentLine = text.get(i);
            if (currentLine.contains(Constants.BRACKET_FIGURE_OPEN)) {
                bracketCounter++;
            }
            if (currentLine.contains(Constants.BRACKET_FIGURE_CLOSE)) {
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

    @NotNull
    private String getValue(@NotNull final List<String> words) {
        return words.get(words.size() - 1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
    }
}
