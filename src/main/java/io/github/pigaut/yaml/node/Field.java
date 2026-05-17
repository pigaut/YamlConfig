package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;

import java.lang.runtime.*;
import java.util.*;

public abstract class Field implements ConfigField {

    private List<CommentLine> blockComments = new ArrayList<>();
    private List<CommentLine> inLineComments = new ArrayList<>();

    @NotNull
    public abstract Branch getParent() throws UnsupportedOperationException;

    public @NotNull String getPath() {
        if (isRoot()) {
            throw new UnsupportedOperationException("Root configurations do not have a path");
        }

        List<Field> branch = new ArrayList<>();
        Field currentNode = this;
        while (!currentNode.isRoot()) {
            branch.add(0, currentNode);
            currentNode = currentNode.getParent();
        }

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < branch.size(); i++) {
            Field node = branch.get(i);
            final StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(node.getKey());

            while (node instanceof Sequence && i < branch.size() - 1) {
                final Field nextNode = branch.get(1 + i++);
                keyBuilder.append(nextNode.getKey());
                node = nextNode;
            }
            keys.add(keyBuilder.toString());
        }

        return String.join(".", keys);
    }

    public @NotNull String getSimplePath() {
        if (isRoot()) {
            throw new UnsupportedOperationException("Root configurations do not have a path");
        }

        List<Field> branch = new ArrayList<>();
        Field currentNode = this;
        while (!currentNode.isRoot()) {
            branch.add(0, currentNode);
            currentNode = currentNode.getParent();
        }

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < branch.size(); i++) {
            Field node = branch.get(i);
            final StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(node instanceof KeylessField keylessNode ?
                    ("[" + keylessNode.getPosition() + "]") : node.getKey());
            while (node instanceof Sequence && i < branch.size() - 1) {
                final KeylessField nextNode = (KeylessField) branch.get(1 + i++);
                keyBuilder.append("[" + nextNode.getPosition() + "]");
                node = (Field) nextNode;
            }
            keys.add(keyBuilder.toString());
        }

        return String.join(".", keys);
    }

    @Override
    public <T> T getRequired(@NotNull Class<T> classType) throws InvalidConfigException {
        return get(classType).orThrow();
    }

    @Override
    public List<CommentLine> getInLineComments() {
        return inLineComments;
    }

    @Override
    public void setInLineComments(@Nullable List<CommentLine> inLineComments) {
        this.inLineComments = inLineComments != null ? new ArrayList<>(inLineComments) : new ArrayList<>();
    }

    @Override
    public void clearInlineComments() {
        inLineComments.clear();
    }

    @Override
    public void addInlineComment(@NotNull String value) {
        inLineComments.add(new CommentLine(Optional.empty(), Optional.empty(), value, CommentType.IN_LINE));
    }

    @Override
    public List<CommentLine> getBlockComments() {
        return blockComments;
    }

    @Override
    public void setBlockComments(@Nullable List<CommentLine> blockComments) {
        this.blockComments = blockComments != null ? new ArrayList<>(blockComments) : new ArrayList<>();
    }

    @Override
    public void clearBlockComments() {
        blockComments.clear();
    }

    @Override
    public void addBlockComment(@NotNull String value) {
        blockComments.add(new CommentLine(Optional.empty(), Optional.empty(), value, CommentType.BLOCK));
    }

    @Override
    public void addBlockBlankLine() {
        blockComments.add(new CommentLine(Optional.empty(), Optional.empty(), "", CommentType.BLANK_LINE));
    }

}
