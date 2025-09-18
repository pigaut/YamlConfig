package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public class KeylessScalar extends Scalar implements KeylessField {

    private final Sequence parent;
    private int index;

    public KeylessScalar(@NotNull Sequence parent, int index, @NotNull Object value) {
        super(value);
        this.parent = parent;
        this.index = index;
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
    public ConfigSequence split(Pattern pattern) {
        final ConfigSequence sequence = new KeylessSequence(parent, index);
        final List<Object> parsedValues = ParseUtil.parseAllAsScalars(pattern.split(toString()));
        sequence.map(parsedValues);
        return sequence;
    }

}
