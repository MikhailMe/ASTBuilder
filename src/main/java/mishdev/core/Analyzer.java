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
        return this.analyzePackage();
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
        ASTNode classASTNode = new ASTNode(packageASTNode, Constants.KEYWORD_CLASS);
        this.analyzeClass(classASTNode);
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
        ASTNode fieldNodes = new ASTNode(classASTNode, Constants.KEYWORD_FIELDS);
        ASTNode methodNodes = new ASTNode(classASTNode, Constants.KEYWORD_METHODS);
        for (int index = diapason.left + 1; index < diapason.right; index++) {
            String classLine = programText.get(index);
            if (classLine.contains(Constants.BRACKET_FIGURE_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_OPEN)
                    && classLine.contains(Constants.BRACKET_ROUND_CLOSE)) {
                ASTNode methodASTNode = preAnalyzer.preAnalyzeMethod(classASTNode, classLine);
                ImmutablePair<Integer, Integer> methodDiapason = this.calculateDiapason(index, programText);
                ASTNode methodBodyNode = new ASTNode(methodASTNode, Constants.KEYWORD_BODY);
                this.analyzeBlock(methodBodyNode, ImmutablePair.of(methodDiapason.left + 1, methodDiapason.right));
                methodASTNode.children.add(methodBodyNode);
                index = methodDiapason.right;
                methodNodes.children.add(methodASTNode);
            } else if (classLine.contains(Constants.SEMICOLON_SYMBOL)) {
                ASTNode fieldNode = this.analyzeField(classASTNode, classLine);
                fieldNodes.children.add(fieldNode);
            }
        }
        if (!fieldNodes.children.isEmpty()) {
            classASTNode.children.add(fieldNodes);
        }
        if (!methodNodes.children.isEmpty()) {
            classASTNode.children.add(methodNodes);
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
        ASTNode fieldModifiers = new ASTNode(fieldASTNode, Constants.KEYWORD_MODIFIERS);
        String previousWord = Constants.EMPTY_SYMBOL;
        for (String currentWord : words) {
            if (currentWord.isEmpty()) {
                continue;
            }
            if (Constants.MODIFIERS.contains(currentWord)) {
                ASTNode modifierNode = new ASTNode(fieldModifiers, currentWord, Constants.KEYWORD_MODIFIER);
                fieldModifiers.children.add(modifierNode);
            } else if (Constants.PRIMITIVE_TYPES.contains(currentWord)
                    || Constants.TYPE_VOID.equals(currentWord)
                    || Character.isUpperCase(currentWord.charAt(0))) {
                ASTNode typeFieldNode = new ASTNode(fieldASTNode, currentWord, Constants.KEYWORD_TYPE);
                fieldASTNode.children.add(typeFieldNode);
            } else if (!Constants.PRIMITIVE_TYPES.contains(currentWord)
                    && Character.isLowerCase(currentWord.charAt(0))) {
                fieldASTNode.name = currentWord.replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
            } else if (previousWord.equals(Constants.EQUAL_SYMBOL)) {
                String data = currentWord.replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
                ASTNode valueFieldNode = new ASTNode(fieldASTNode, data, Constants.KEYWORD_CONST);
                fieldASTNode.children.add(valueFieldNode);
            }
            previousWord = currentWord;
        }
        if (!fieldModifiers.children.isEmpty()) {
            fieldASTNode.children.add(fieldModifiers);
        }
        return fieldASTNode;
    }

    private void analyzeBlock(@NotNull final ASTNode blockNode,
                              @NotNull final ImmutablePair<Integer, Integer> diapason) {
        for (int index = diapason.left; index < diapason.right; index++) {
            String methodLine = programText.get(index);
            List<String> lineWords = Arrays
                    .stream(methodLine.split(Constants.SPACE_SYMBOL))
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.toList());

            ASTNode node = new ASTNode(blockNode);
            if (checker.isDeclareVariable(lineWords)) {
                this.analyzeDeclareVariable(node, lineWords);
            } else if (checker.isExpression(lineWords)) {
                this.analyzeAssignment(node, lineWords);
            } else if (checker.isConditionStatement(lineWords)) {
                index = this.analyze(index, blockNode, lineWords, this::preAnalyzeConditionStatement);
            } else if (checker.isCycleFor(lineWords)) {
                index = this.analyze(index, blockNode, lineWords, this::preAnalyzeCycle);
            } else if (checker.isReturn(lineWords)) {
                node.keyWord = Constants.IDENTIFIER_RETURN;
                ASTNode returnValue = new ASTNode(node, Constants.KEYWORD_RETURN_VALUE);
                returnValue.data = this.getValue(lineWords);
                node.children.add(returnValue);
            }
            if (node.isUsed()) {
                blockNode.children.add(node);
            }
        }
    }

    @NotNull
    private ASTNode preAnalyzeCycle(@NotNull final ASTNode blockASTNode,
                                    @NotNull final List<String> words) {
        String value = words.get(0);
        ASTNode cycleASTNode = new ASTNode(blockASTNode, value, Constants.KEYWORD_CYCLE);

        String cycleString = String.join(Constants.SPACE_SYMBOL, words);
        int firstIndex = cycleString.indexOf(Constants.BRACKET_ROUND_OPEN);
        int lastIndex = cycleString.lastIndexOf(Constants.BRACKET_ROUND_CLOSE);

        String cycleBody = cycleString.substring(firstIndex + 1, lastIndex);
        String[] tokens = cycleBody.split(Constants.SEMICOLON_SYMBOL);

        ASTNode cycleParameters = new ASTNode(cycleASTNode, Constants.KEYWORD_CYCLE_PARAMETERS);
        for (String token : tokens) {
            ASTNode childASTNode = new ASTNode(cycleASTNode);
            this.analyzeToken(childASTNode, Arrays
                    .stream(token.split(Constants.SPACE_SYMBOL))
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList()));
            cycleParameters.children.add(childASTNode);
        }
        cycleASTNode.children.add(cycleParameters);
        return cycleASTNode;
    }

    @NotNull
    private ASTNode preAnalyzeConditionStatement(@NotNull final ASTNode blockASTNode,
                                                 @NotNull final List<String> words) {
        ASTNode simpleConditionASTNode = new ASTNode(blockASTNode, Constants.IDENTIFIER_IF, Constants.KEYWORD_CONDITION);
        String conditionString = String.join(Constants.SPACE_SYMBOL, words);
        int firstIndex = conditionString.indexOf(Constants.BRACKET_ROUND_OPEN);
        int lastIndex = conditionString.lastIndexOf(Constants.BRACKET_ROUND_CLOSE);

        String condition = conditionString.substring(firstIndex + 1, lastIndex);
        this.analyzeSimpleCondition(simpleConditionASTNode, Arrays
                .stream(condition.split(Constants.SPACE_SYMBOL))
                .collect(Collectors.toList()), true);

        return simpleConditionASTNode;
    }

    private void analyzeToken(@NotNull final ASTNode node,
                              @NotNull final List<String> words) {
        if (checker.isDeclareVariable(words)) {
            this.analyzeDeclareVariable(node, words);
        } else if (checker.isExpression(words)) {
            this.analyzeAssignment(node, words);
        } else if (checker.isSimpleCondition(words)) {
            this.analyzeSimpleCondition(node, words, false);
        }
    }

    // BASE METHODS

    private void analyzeSimpleCondition(@NotNull final ASTNode conditionStatement,
                                        @NotNull final List<String> words,
                                        boolean toParameters) {
        if (toParameters) {
            ASTNode conditionSimpleASTNode = new ASTNode(conditionStatement, Constants.KEYWORD_OPERATOR);
            this.getConditionChildren(conditionSimpleASTNode, words);
            conditionStatement.children.add(conditionSimpleASTNode);
        } else {
            conditionStatement.keyWord = Constants.KEYWORD_OPERATOR;
            this.getConditionChildren(conditionStatement, words);
        }
    }

    private void getConditionChildren(@NotNull final ASTNode node,
                                      @NotNull final List<String> words) {
        node.data = words.get(1);
        this.createChild(node, words.get(0));
        this.createChild(node, words.get(2).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL));
    }

    private void analyzeDeclareVariable(@NotNull final ASTNode declareVariableNode,
                                        @NotNull final List<String> words) {
        declareVariableNode.keyWord = Constants.KEYWORD_DECLARE_VARIABLE;
        String data = words.get(0);
        ASTNode typeNode = new ASTNode(declareVariableNode, data, Constants.KEYWORD_TYPE);
        declareVariableNode.children.add(typeNode);

        if (words.get(1).contains(Constants.SEMICOLON_SYMBOL)) {
            declareVariableNode.name = words.get(1).replace(Constants.SEMICOLON_SYMBOL, Constants.EMPTY_SYMBOL);
        } else {
            declareVariableNode.name = words.get(1);
            ASTNode assignmentNode = new ASTNode(declareVariableNode, Constants.EQUAL_SYMBOL, Constants.KEYWORD_ASSIGNMENT);
            this.analyzeInnerExpression(assignmentNode, words);
            declareVariableNode.children.add(assignmentNode);
        }
    }

    private void analyzeAssignment(@NotNull final ASTNode expressionNode,
                                   @NotNull final List<String> words) {
        if (words.contains(Constants.EQUAL_SYMBOL)) {
            int equalIndex = words.indexOf(Constants.EQUAL_SYMBOL);
            expressionNode.data = words.get(equalIndex);
            expressionNode.keyWord = Constants.KEYWORD_ASSIGNMENT;
            this.createChild(expressionNode, words.get(0));
            this.analyzeInnerExpression(expressionNode, words);
        } else if (words.size() == 1) { // analyzing unary operators
            String word = words.get(0);
            String detected = word.substring(word.length() - 2);
            if (Constants.UNARY_OPERATORS.contains(detected)) {
                expressionNode.data = detected;
                expressionNode.keyWord = Constants.KEYWORD_OPERATOR;
                String data = word.substring(0, word.length() - 2);
                ASTNode variableNode = new ASTNode(expressionNode, data, Constants.KEYWORD_VARIABLE);
                expressionNode.children.add(variableNode);
            }
        }
    }

    private void analyzeInnerExpression(@NotNull final ASTNode expressionNode,
                                        @NotNull final List<String> words) {
        int operatorIndex = -1;
        for (String operator : Constants.ARITHMETIC_OPERATORS) {
            int index = words.indexOf(operator);
            if (index != -1) {
                operatorIndex = index;
                break;
            }
        }

        if (operatorIndex == -1) {
            this.createChild(expressionNode, getValue(words));
        } else {
            String operatorValue = words.get(operatorIndex);
            ASTNode operatorNode = new ASTNode(expressionNode, operatorValue, Constants.KEYWORD_OPERATOR);
            List<String> subWords = words.subList(operatorIndex + 1, words.size());
            this.createChild(operatorNode, words.get(operatorIndex - 1));
            if (subWords.size() == 1) {
                this.createChild(operatorNode, getValue(words.subList(operatorIndex - 1, words.size())));
                expressionNode.children.add(operatorNode);
            } else if (subWords.size() > 1) {
                this.analyzeInnerExpression(operatorNode, subWords);
                expressionNode.children.add(operatorNode);
            }
        }
    }

    private void createChild(@NotNull final ASTNode parent,
                             @NotNull final String value) {
        String keyWord = this.getKeyWord(value);
        ASTNode childNode = new ASTNode(parent, value, keyWord);
        parent.children.add(childNode);
    }

    private int analyze(final int index,
                        @NotNull final ASTNode blockASTNode,
                        @NotNull final List<String> words,
                        @NotNull BiFunction<ASTNode, List<String>, ASTNode> preAnalyze) {
        ImmutablePair<Integer, Integer> diapason = this.calculateDiapason(index, programText);
        ASTNode node = preAnalyze.apply(blockASTNode, words);
        ASTNode bodyNode = new ASTNode(node, Constants.KEYWORD_BODY);
        this.analyzeBlock(bodyNode, ImmutablePair.of(diapason.left + 1, diapason.right));
        node.children.add(bodyNode);
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

    @NotNull
    private String getKeyWord(@NotNull final String value) {
        for (int i = 0; i < value.length(); ++i) {
            char currentChar = value.charAt(i);
            if (!Character.isDigit(currentChar) && currentChar != '.') {
                return Constants.KEYWORD_VARIABLE;
            }
        }
        return Constants.KEYWORD_CONST;
    }
}
