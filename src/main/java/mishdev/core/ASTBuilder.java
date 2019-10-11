package mishdev.core;

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

}
