package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;

public class KeylessSection extends Section implements KeylessField {

    private final Sequence parent;
    private int index;

    public KeylessSection(@NotNull Sequence parent, int index) {
        super(FlowStyle.BLOCK);
        this.parent = parent;
        this.index = index;
        final FlowStyle defaultStyle = parent.getNestedFlowStyle();
        if (defaultStyle != null) {
            setFlowStyle(defaultStyle);
        }
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getPosition() {
        return index + 1;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public @NotNull String getKey() throws UnsupportedOperationException {
        return "[" + index + "]";
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public @NotNull Sequence getParent() throws UnsupportedOperationException {
        return parent;
    }

    @Override
    public @NotNull ConfigRoot getRoot() {
        return parent.getRoot();
    }

    @Override
    public @NotNull Sequence convertToSequence() {
        Sequence sequence = new KeylessSequence(parent, index);
        parent.set(index, sequence);
        return sequence;
    }

}
