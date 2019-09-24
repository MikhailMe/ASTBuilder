package mishdev.core;

import mishdev.core.analyzer.Analyzer;
import mishdev.core.analyzer.IAnalyzer;
import mishdev.core.reader.IReader;
import mishdev.core.reader.Reader;

import java.util.HashMap;
import java.util.Map;

public class ASTBuilder {

    private IReader reader;
    private IAnalyzer analyzer;

    public ASTBuilder() {
        reader = new Reader();
        analyzer = new Analyzer();
    }

    public Map<String, Map> build(String fileName) {
        reader.init(fileName);
        return new HashMap<>();
    }

}
