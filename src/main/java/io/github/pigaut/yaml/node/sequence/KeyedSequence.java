package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.section.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;

public class KeyedSequence extends Sequence {

    private final Section parent;
    private final String key;

    public KeyedSequence(@NotNull Section parent, @NotNull String key) {
        super(FlowStyle.BLOCK);
        this.parent = parent;
        this.key = key;
        final FlowStyle defaultStyle = parent.getNestedFlowStyle();
        if (defaultStyle != null) {
            setFlowStyle(defaultStyle);
        }
    }

    public KeyedSequence(@NotNull Section parent, @NotNull String key, @NotNull List<@NotNull Object> elements) {
        this(parent, key);
        this.map(elements);
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
    public @NotNull ConfigRoot getRoot() {
        return parent.getRoot();
    }

    @Override
    public @NotNull Section convertToSection() {
        final Section section = new KeyedSection(parent, key, toMap());
        parent.putNode(key, section);
        return section;
    }

}
