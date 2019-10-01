package mishdev.core.models;

import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

@Data
public class StructuredProgram {

    public int filePackage;
    public ImmutablePair<Integer, Integer> fileClass;
    public List<Integer> fields;
    public List<ImmutablePair<Integer, Integer>> methods;

    public StructuredProgram() {
        filePackage = -1;
        fileClass = ImmutablePair.of(-1, -1);
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public void printAll() {
        printPackages();
        printClasses();
        printFields();
        printMethods();
    }

    public void printPackages() {
        System.out.println("package:");
        System.out.println("on the " + filePackage);
    }

    public void printClasses() {
        System.out.println("classes:");
        System.out.println("from " + fileClass.left + " to " + fileClass.right);
    }

    public void printFields() {
        System.out.println("fields:");
        fields.forEach(field -> System.out.println("on the " + field));
    }

    public void printMethods() {
        System.out.println("methods:");
        methods.forEach(p -> System.out.println("from " + p.left + " to " + p.right));
    }
}
