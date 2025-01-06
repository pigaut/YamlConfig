package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

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

}
