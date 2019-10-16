import mishdev.core.ASTBuilder;
import mishdev.core.ASTNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class AnalyzerTest {

    private ProgramGenerator generator;

    @Before
    public void beforeAll() {
        generator = new ProgramGenerator();
    }

    @Test
    public void analyzeManyFieldsTest() {
        for (int i = 1; i <= 10; i++) {
            List<String> programWithOneField = generator.generateProgramOnlyWithFields(i);
            ASTNode fullAST = new ASTBuilder(programWithOneField).build();
            ASTNode classAST = fullAST.children.get(0);
            Assert.assertEquals(classAST.children.size(), i);
        }
    }

    @Test
    public void analyzeManyMethodsWithParametersTest() {
        for (int i = 1; i <= 5; i++) {
            List<String> programWithOneField = generator.generateProgramOnlyWithMethodsWithParameters(i);
            ASTNode fullAST = new ASTBuilder(programWithOneField).build();
            ASTNode classAST = fullAST.children.get(0);
            Assert.assertEquals(classAST.children.size(), i);
        }
    }

    @Test
    public void analyzeManyMethodsWithoutParametersTest() {
        for (int i = 1; i <= 10; i++) {
            List<String> programWithOneField = generator.generateProgramOnlyWithMethodsWithoutParameters(i);
            ASTNode fullAST = new ASTBuilder(programWithOneField).build();
            ASTNode classAST = fullAST.children.get(0);
            Assert.assertEquals(classAST.children.size(), i);
        }
    }

}
