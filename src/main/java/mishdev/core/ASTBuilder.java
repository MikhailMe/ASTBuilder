package mishdev.core;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ASTBuilder {

    private Reader reader;
    private Analyzer analyzer;

    public ASTBuilder(@NotNull final String fileName) {
        reader = new Reader();
        analyzer = new Analyzer(reader.read(fileName));
    }

    public ASTBuilder(@NotNull final List<String> programText) {
        reader = new Reader();
        analyzer = new Analyzer(programText);
    }

    @NotNull
    public Node build() {
        Node packageNode = analyzer.analyzePackage();
        Node classNode = new Node(packageNode);
        packageNode.children.add(analyzer.analyzeClass(classNode));
        return packageNode;
    }

}
