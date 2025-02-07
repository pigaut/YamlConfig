package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class Field implements ConfigField {

    @NotNull
    public abstract Branch getParent() throws UnsupportedOperationException;

    @Nullable
    public String getPath() {
        final Field[] branch = getBranch();
        final List<String> keys = new ArrayList<>();
        for (int i = 0; i < branch.length; i++) {
            Field currentNode = branch[i];
            final StringBuilder keyBuilder = new StringBuilder(currentNode.getKey());
            while (currentNode instanceof Sequence && i < branch.length - 1) {
                final Field nextNode = branch[1 + i++];
                keyBuilder.append(nextNode.getKey());
                currentNode = nextNode;
            }
            keys.add(keyBuilder.toString());
        }
        return String.join(".", keys);
    }

    @NotNull
    public String getPath(String key) {
        return getPath() + "." + key;
    }

    @Override
    public <T> Optional<T> loadOptional(Class<T> type) {
        return ConfigOptional.of(() -> load(type));
    }

    protected Field[] getBranch() {
        final List<Field> nodeTree = new ArrayList<>();

        Field currentNode = this;
        while (!currentNode.isRoot()) {
            nodeTree.add(0, currentNode);
            currentNode = currentNode.getParent();
        }

        return nodeTree.toArray(new Field[0]);
    }

    @Override
    public Optional<ConfigScalar> asScalar() {
        return ConfigOptional.of(this::toScalar);
    }

    @Override
    public Optional<ConfigSection> asSection() {
        return ConfigOptional.of(this::toSection);
    }

    @Override
    public Optional<ConfigSequence> asSequence() {
        return ConfigOptional.of(this::toSequence);
    }

}
