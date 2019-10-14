package mishdev.core;

import mishdev.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public Node build() {
        return this.analyzer.analyzeProgram();
    }

    public void show() {
        this.traversalAST(this.build(), 0);
    }

    //  sequence: keyword -> modifiers -> type -> name -> value -> parameters -> children
    private void traversalAST(final Node node, int tabIndex) {
        System.out.println(Constants.NEXT_STRING_SYMBOL);
        if (node != null) {
            if (node.keyWord != null) {
                System.out.print(generateStringWithTab(node.keyWord + ": ", tabIndex));
            }
            if (node.modifiers != null && !node.modifiers.isEmpty()) {
                node.modifiers.forEach(modifier ->
                        System.out.print(modifier + Constants.SPACE_SYMBOL));
            }
            if (node.type != null) {
                System.out.print(node.type + Constants.SPACE_SYMBOL);
            }
            if (node.name != null) {
                System.out.print(node.name + Constants.SPACE_SYMBOL);
            }
            if (node.value != null) {
                System.out.print(node.value + Constants.SPACE_SYMBOL);
            }
            if (node.parameters != null && !node.parameters.isEmpty()) {
                final int parametersTabIndex = ++tabIndex;
                node.parameters.forEach(param -> this.traversalAST(param, parametersTabIndex));
            }
            if (node.children != null && !node.children.isEmpty()) {
                final int childrenTabIndex = ++tabIndex;
                node.children.forEach(child -> this.traversalAST(child, childrenTabIndex));
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
