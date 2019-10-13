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

    // FIXME: 10/14/2019 
    // keyword -> modifiers -> type -> name -> value -> parameters -> children
    private void traversalAST(final Node node, int tabIndex) {
        if (node != null) {
            if (node.keyWord != null) {
                System.out.print(generateStringWithTab(node.keyWord + ": ", tabIndex));
            }
            if (node.modifiers != null && !node.modifiers.isEmpty()) {
                final int index = tabIndex;
                node.modifiers.forEach(modifier ->
                        System.out.print(generateStringWithTab(modifier + Constants.SPACE_SYMBOL, index)));
            }
            if (node.type != null) {
                System.out.print(generateStringWithTab(node.type + Constants.SPACE_SYMBOL, tabIndex));
            }
            if (node.name != null) {
                System.out.print(generateStringWithTab(node.name + Constants.SPACE_SYMBOL, tabIndex));
            }
            if (node.value != null) {
                System.out.print(generateStringWithTab(node.value + Constants.SPACE_SYMBOL, tabIndex));
            }
            if (node.parameters != null && !node.parameters.isEmpty()) {
                System.out.println(Constants.NEXT_STRING_SYMBOL);
                final int parametersTabIndex = tabIndex++;
                node.parameters.forEach(param -> this.traversalAST(param, parametersTabIndex));
            }
            if (node.children != null && !node.children.isEmpty()) {
                System.out.println(Constants.NEXT_STRING_SYMBOL);
                final int childrenTabIndex = tabIndex++;
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
