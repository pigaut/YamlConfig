package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.section.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;

public class KeylessSequence extends Sequence implements KeylessField {

    private final Sequence parent;
    private int index;

    public KeylessSequence(@NotNull Sequence parent, int index) {
        super(FlowStyle.FLOW);
        this.index = index;
        this.parent = parent;
        final FlowStyle defaultStyle = parent.getNestedFlowStyle();
        if (defaultStyle != null) {
            setFlowStyle(defaultStyle);
        }
    }

    public KeylessSequence(@NotNull Sequence parent, int index, @NotNull List<@NotNull Object> elements) {
        this(parent, index);
        this.map(elements);
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public @NotNull ConfigRoot getRoot() {
        return parent.getRoot();
    }

    @Override
    public @NotNull String getKey() throws UnsupportedOperationException {
        return "[" + index + "]";
    }

    @Override
    public @NotNull Sequence getParent() throws UnsupportedOperationException {
        return parent;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public @NotNull Section convertToSection() {
        final Section section = new KeylessSection(parent, index, toMap());
        parent.set(index, section);
        return section;
    }

}
