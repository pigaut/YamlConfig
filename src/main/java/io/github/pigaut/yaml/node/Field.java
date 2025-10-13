package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class Field implements ConfigField {

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
    public <T> T loadRequired(Class<T> classType) {
        return load(classType).orThrow();
    }

}
