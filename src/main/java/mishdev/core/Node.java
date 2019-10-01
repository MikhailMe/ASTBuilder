package mishdev.core;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Node {

    public Node parent;
    public List<Node> children;
    public List<Node> parameters;

    public String name;

    public String type;

    public String keyWord;

    public List<String> modifiers;

    public Node() {
        this.children = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    public Node(final Node parent) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    @Override
    public String toString() {
        return  "AST:\n" +
                "name: " + name + "\n" +
                "type: " + (type == null ? "null" : type) + "\n" +
                "keyword: " + keyWord + "\n" +
                "modifiers: " + (modifiers == null ? "null" : modifiers.toString());
    }

}
