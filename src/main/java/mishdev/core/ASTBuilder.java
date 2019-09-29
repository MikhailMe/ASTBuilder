package mishdev.core;

import com.google.common.collect.TreeBasedTable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.List;

public class ASTBuilder {

    private Reader reader;
    private Detector detector;

    public ASTBuilder() {
        reader = new Reader();
    }

    public void build(String fileName) {
        List<String> programText = reader.read(fileName);
        detector = new Detector(programText);
        detector.detect();
        detector.getStructuredProgram().printAll();

    }

}
