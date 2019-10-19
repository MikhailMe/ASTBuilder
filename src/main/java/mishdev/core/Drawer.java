package mishdev.core;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import mishdev.util.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class Drawer {

    void drawAST(@NotNull final ASTNode ast) {
        this.createDotFileByAST(ast);
        try {
            MutableGraph graph = Parser.read(new File(Constants.DOT_FILE_LOCATION));
            Graphviz
                    .fromGraph(graph.setDirected(true))
                    .width(2500)
                    .height(2500)
                    .render(Format.SVG)
                    .toFile(new File(Constants.OUTPUT_LOCATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDotFileByAST(@NotNull final ASTNode ast) {
        List<Pair<Long, List<String>>> dots = new ArrayList<>();
        List<Pair<String, String>> links = new ArrayList<>();
        this.prepareDrawingData(ast, links, dots);

        String declareString = generateDeclareString(dots);
        String linksString = generateLinksString(links);
        this.generateASTDotFile(declareString, linksString);
    }

    private void prepareDrawingData(final ASTNode node,
                                    List<Pair<String, String>> links,
                                    List<Pair<Long, List<String>>> dots) {
        if (node != null) {
            final long dotId = node.id;
            List<String> label = new ArrayList<>(List.of(node.keyWord, Constants.COLON_SYMBOL));
            if (node.data != null) {
                label.add(node.data);
            }
            if (node.children != null && !node.children.isEmpty()) {
                node.children.forEach(child -> {
                    links.add(ImmutablePair.of(String.valueOf(dotId), String.valueOf(child.id)));
                    prepareDrawingData(child, links, dots);
                });
            }
            dots.add(ImmutablePair.of(dotId, label));
        }
    }

    private void generateASTDotFile(@NotNull final String declareString,
                                    @NotNull final String linksString) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.KEYWORD_DIGRAPH)
                .append(Constants.BRACKET_FIGURE_OPEN)
                .append(Constants.NEXT_STRING_SYMBOL)
                .append(declareString)
                .append(linksString)
                .append(Constants.NEXT_STRING_SYMBOL)
                .append(Constants.BRACKET_FIGURE_CLOSE);
        try {
            Files.write(Paths.get(Constants.DOT_FILE_LOCATION), sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private String generateDeclareString(@NotNull final List<Pair<Long, List<String>>> dots) {
        StringBuilder sb = new StringBuilder();

        dots.forEach(dot -> sb.append(dot.getLeft())
                .append(Constants.BRACKET_SQUARE_OPEN)
                .append(Constants.KEYWORD_LABEL)
                .append(Constants.EQUAL_SYMBOL)
                .append(Constants.QUOTE_SYMBOL)
                .append(String.join(Constants.SPACE_SYMBOL, dot.getRight()))
                .append(Constants.QUOTE_SYMBOL)
                .append(Constants.BRACKET_SQUARE_CLOSE)
                .append(Constants.SEMICOLON_SYMBOL)
                .append(Constants.NEXT_STRING_SYMBOL));

        return sb.toString();
    }

    @NotNull
    private String generateLinksString(@NotNull final List<Pair<String, String>> links) {
        StringBuilder sb = new StringBuilder();

        links.forEach(link -> sb.append(link.getLeft())
                .append(Constants.SPACE_SYMBOL)
                .append(Constants.ARROW_SYMBOL)
                .append(Constants.SPACE_SYMBOL)
                .append(link.getRight())
                .append(Constants.NEXT_STRING_SYMBOL));

        return sb.toString();
    }
}
