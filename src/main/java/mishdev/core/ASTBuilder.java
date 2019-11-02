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

    public void draw() {
        ASTNode ast = this.build();
        new Drawer().drawAST(ast);
    }

    private void traversalAST(final ASTNode node, int tabIndex) {
        System.out.println(Constants.NEXT_STRING_SYMBOL);
        if (node != null) {
            if (node.keyWord != null) {
                System.out.print(this.generateStringWithTab(node.keyWord + ": ", tabIndex));
            }
            if (node.data != null) {
                System.out.print(node.data + Constants.SPACE_SYMBOL);
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
            sb.append(Constants.TAB_SYMBOL);
        }
        sb.append(string);
        return sb.toString();
    }

}
