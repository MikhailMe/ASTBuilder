package mishdev.core;

import lombok.Data;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ASTNode {

    public ASTNode parent;
    public List<ASTNode> children;
    public List<ASTNode> parameters;

    public String name;
    public String type;
    public Object value;
    public String keyWord;
    public List<String> modifiers;

    ASTNode() {
        this.children = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    ASTNode(final ASTNode parent) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    ASTNode(final ASTNode parent,
            @NotNull final String keyWord) {
        this.parent = parent;
        this.keyWord = keyWord;
        this.children = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    boolean isUsed() {
        return !children.isEmpty()
                || !parameters.isEmpty()
                || name != null
                || type != null
                || value != null
                || keyWord != null
                || !modifiers.isEmpty();
    }

    @Override
    public String toString() {
        return "AST:\n" +
                "name: " + name + "\n" +
                "type: " + (type == null ? "null" : type) + "\n" +
                "keyword: " + keyWord + "\n" +
                "modifiers: " + (modifiers == null ? "null" : modifiers.toString());
    }

}
