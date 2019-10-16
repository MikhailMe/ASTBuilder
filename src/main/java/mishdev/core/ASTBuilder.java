package mishdev.core;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.parse.Parser;
import mishdev.util.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTBuilder {

    @NotNull
    private Analyzer analyzer;

    public ASTBuilder(@NotNull final String pathToProgram) {
        this.analyzer = new Analyzer(new Reader().read(pathToProgram));
    }

    public ASTBuilder(@NotNull final List<String> program) {
        this.analyzer = new Analyzer(program);
    }

    @NotNull
    public ASTNode build() {
        return this.analyzer.analyzeProgram();
    }

    public void show() {
        this.traversalAST(this.build(), 0);
    }

    public void draw() {
        List<Pair<String, List<String>>> dots = new ArrayList<>();
        List<Pair<String, String>> links = new ArrayList<>();
        this.generateASTDotFile(this.build(), links, dots);
        try {
            MutableGraph g = Parser.read(new File("src\\main\\resources\\ast.dot"));
            Graphviz.fromGraph(g.setDirected(true)).width(700).render(Format.PNG).toFile(new File("src\\main\\resources\\output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateASTDotFile(final ASTNode node,
                                   List<Pair<String, String>> links,
                                   List<Pair<String, List<String>>> dots) {
        if (node != null) {
            final String dotName = node.keyWord;
            List<String> label = new ArrayList<>();
            if (node.modifiers != null && !node.modifiers.isEmpty()) {
                node.modifiers.forEach(modifier -> {
                    label.add(modifier);
                    System.out.print(modifier + Constants.SPACE_SYMBOL);
                });
            }
            if (node.type != null) {
                label.add(node.type);
                System.out.print(node.type + Constants.SPACE_SYMBOL);
            }
            if (node.name != null) {
                label.add(node.name);
                System.out.print(node.name + Constants.SPACE_SYMBOL);
            }
            if (node.value != null) {
                label.add(node.value.toString());
                System.out.print(node.value + Constants.SPACE_SYMBOL);
            }
            if (node.parameters != null && !node.parameters.isEmpty()) {
                node.parameters.forEach(param -> {
                    links.add(ImmutablePair.of(dotName, param.name));
                    generateASTDotFile(param, links, dots);
                });
            }
            if (node.children != null && !node.children.isEmpty()) {
                node.children.forEach(child -> {
                    links.add(ImmutablePair.of(dotName, child.name));
                    generateASTDotFile(child, links, dots);
                });
            }
            dots.add(ImmutablePair.of(dotName, label));
        }

        // write to file
    }

    //  sequence: keyword -> modifiers -> type -> name -> value -> parameters -> children
    private void traversalAST(final ASTNode ASTNode, int tabIndex) {
        System.out.println(Constants.NEXT_STRING_SYMBOL);
        if (ASTNode != null) {
            if (ASTNode.keyWord != null) {
                System.out.print(generateStringWithTab(ASTNode.keyWord + ": ", tabIndex));
            }
            if (ASTNode.modifiers != null && !ASTNode.modifiers.isEmpty()) {
                ASTNode.modifiers.forEach(modifier ->
                        System.out.print(modifier + Constants.SPACE_SYMBOL));
            }
            if (ASTNode.type != null) {
                System.out.print(ASTNode.type + Constants.SPACE_SYMBOL);
            }

            if (ASTNode.value != null) {
                System.out.print(ASTNode.value + Constants.SPACE_SYMBOL);
            }
            if (ASTNode.parameters != null && !ASTNode.parameters.isEmpty()) {
                final int parametersTabIndex = ++tabIndex;
                ASTNode.parameters.forEach(param -> this.traversalAST(param, parametersTabIndex));
            }
            if (ASTNode.children != null && !ASTNode.children.isEmpty()) {
                final int childrenTabIndex = ++tabIndex;
                ASTNode.children.forEach(child -> this.traversalAST(child, childrenTabIndex));
            }
        }
    }

    @NotNull
    private String generateStringWithTab(@NotNull final String string, final int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("    ");
        }
        sb.append(string);
        return sb.toString();
    }

}
