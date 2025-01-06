package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;

public class KeyedSection extends Section {

    private final Section parent;
    private final String key;

    public KeyedSection(@NotNull Section parent, @NotNull String key) {
        super(FlowStyle.BLOCK);
        this.parent = parent;
        this.key = key;
        final FlowStyle defaultStyle = parent.getNestedFlowStyle();
        if (defaultStyle != null) {
            setFlowStyle(defaultStyle);
        }
    }

    public KeyedSection(@NotNull Section parent, @NotNull String key, @NotNull Map<String, @NotNull Object> mappings) {
        this(parent, key);
        this.map(mappings);
    }

    @Override
    public @NotNull ConfigRoot getRoot() {
        return parent.getRoot();
    }

    @Override
    public @NotNull String getKey() throws UnsupportedOperationException {
        return key;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public @NotNull Section getParent() throws UnsupportedOperationException {
        return parent;
    }

    @Override
    public @NotNull Sequence convertToSequence() {
        final Sequence sequence = new KeyedSequence(parent, key, toList());
        parent.putNode(key, sequence);
        return sequence;
    }

}
