package io.github.pigaut.yamlib.config.node;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;

public class ChildSection extends SectionNode {

    private final SectionNode parent;
    private final String key;

    private ChildSection(@NotNull SectionNode parent, @NotNull String key, @Nullable FlowStyle flowStyle) {
        Preconditions.checkNotNull(parent, "parent section cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        this.parent = parent;
        this.key = key;
        if (flowStyle != null) {
            setFlowStyle(flowStyle);
        }
    }

    public static ChildSection ofSection(@NotNull SectionNode parent, @NotNull String key) {
        final ChildSection section = new ChildSection(parent, key, parent.getDefaultFlowStyle());
        parent.children.put(key, section);
        return section;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public @NotNull Config getRoot() {
        return parent.getRoot();
    }

    @Override
    public @NotNull SectionNode getParent() {
        return parent;
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }

    private ConfigSection[] getBranch() {
        List<ConfigSection> nodeTree = new ArrayList<>();

        ConfigSection currentNode = this;

        while (!currentNode.isRoot()) {
            nodeTree.add(0, currentNode);
            currentNode = currentNode.getParent();
        }

        return nodeTree.toArray(new ConfigSection[0]);
    }

    public @NotNull String getPath() {
        ConfigSection[] branch = getBranch();
        ConfigSection currentNode = this;

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < branch.length; i++) {
            currentNode = branch[i];
            String key = currentNode.getKey();

            while (currentNode.isKeyless() && i < branch.length - 1) {
                ConfigSection child = branch[1 + i++];

                key = key + "[" + child.getKey() + "]";
                currentNode = child;
            }

            keys.add(key);
        }

        return String.join(".", keys);
    }

}
