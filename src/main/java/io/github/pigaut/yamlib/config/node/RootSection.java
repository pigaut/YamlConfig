package io.github.pigaut.yamlib.config.node;

import io.github.pigaut.yamlib.*;
import org.jetbrains.annotations.*;

public abstract class RootSection extends SectionNode implements Config {

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public @NotNull Config getRoot() {
        return this;
    }

    @Override
    public @NotNull ConfigSection getParent() {
        throw new UnsupportedOperationException("Root section does not have a parent.");
    }

    @Override
    public @NotNull String getKey() {
        throw new UnsupportedOperationException("Root section does not have a key.");
    }

    @Override
    public @NotNull String getPath() {
        throw new UnsupportedOperationException("Root section does not have a path.");
    }

}
