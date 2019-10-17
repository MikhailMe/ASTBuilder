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
    public ASTNode build() {
        return this.analyzer.analyzeProgram();
    }

    public void showInConsole() {
        this.traversalAST(this.build(), 0);
    }

    public void draw() {
        new Drawer(this.build()).drawAST();
    }

    //  sequence: keyword -> modifiers -> type -> name -> value -> parameters -> children
    private void traversalAST(final ASTNode ASTNode, int tabIndex) {
        System.out.println(Constants.NEXT_STRING_SYMBOL);
        if (ASTNode != null) {
            if (ASTNode.keyWord != null) {
                System.out.print(this.generateStringWithTab(ASTNode.keyWord + ": ", tabIndex));
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
