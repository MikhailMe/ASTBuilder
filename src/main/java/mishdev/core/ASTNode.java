package mishdev.core;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ASTNode {

    public ASTNode parent;
    public List<ASTNode> children;

    public long id;
    public String data;
    public String keyWord;

    private static long nodeId = -1;

    private long incrementId() {
        ++nodeId;
        return nodeId;
    }

    ASTNode() {
        this.id = incrementId();
        this.children = new ArrayList<>();
    }

    ASTNode(final ASTNode parent) {
        this.id = incrementId();
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    ASTNode(final ASTNode parent,
            @NotNull final String keyWord) {
        this.parent = parent;
        this.id = incrementId();
        this.keyWord = keyWord;
        this.children = new ArrayList<>();
    }

    ASTNode(final ASTNode parent,
            @NotNull final String data,
            @NotNull final String keyWord) {
        this.data = data;
        this.parent = parent;
        this.id = incrementId();
        this.keyWord = keyWord;
        this.children = new ArrayList<>();
    }

    boolean isUsed() {
        return !children.isEmpty()
                || data != null
                || keyWord != null;
    }

    @Override
    public String toString() {
        return "AST:\n" +
                "data: " + data + "\n" +
                "keyword: " + keyWord;
    }

}
