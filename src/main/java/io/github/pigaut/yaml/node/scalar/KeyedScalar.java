package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.section.*;
import org.jetbrains.annotations.*;

public class KeyedScalar extends Scalar {

    private final Section parent;
    private final String key;

    public KeyedScalar(@NotNull Section parent, @NotNull String key, @NotNull Object value) {
        super(value);
        this.parent = parent;
        this.key = key;
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

}
