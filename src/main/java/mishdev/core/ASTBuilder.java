package mishdev.core;

import mishdev.core.models.StructuredProgram;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ASTBuilder {

    private Node ast;
    private Reader reader;
    private Detector detector;
    private Analyzer analyzer;

    public ASTBuilder(@NotNull final String fileName) {
        ast = new Node();
        reader = new Reader();
        List<String> programText = reader.read(fileName);
        detector = new Detector(programText);
        analyzer = new Analyzer(programText);
    }

    public Node build() {
        StructuredProgram sp = detector.detect();
        ast = analyzer.analyzePackage(sp.filePackage);
        Node classNode = new Node(ast);
        ast.children.add(analyzer.analyzeClass(classNode, sp.fileClass));
        return ast;
    }

}
