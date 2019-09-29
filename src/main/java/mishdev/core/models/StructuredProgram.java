package mishdev.core.models;

import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

@Data
public class StructuredProgram {

    public List<ImmutablePair<Integer, Integer>> packages;
    public List<ImmutablePair<Integer, Integer>> classes;
    public List<ImmutablePair<Integer, Integer>> fields;
    public List<ImmutablePair<Integer, Integer>> methods;

    {
        packages = new ArrayList<>();
        classes = new ArrayList<>();
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
        System.out.println("packages:");
        packages.forEach(p -> System.out.println("from " + p.left + " to " + p.right));
    }

    public void printClasses() {
        System.out.println("classes:");
        classes.forEach(p -> System.out.println("from " + p.left + " to " + p.right));
    }

    public void printFields() {
        System.out.println("fields:");
        fields.forEach(p -> System.out.println("from " + p.left + " to " + p.right));
    }

    public void printMethods() {
        System.out.println("methods:");
        methods.forEach(p -> System.out.println("from " + p.left + " to " + p.right));
    }
}
