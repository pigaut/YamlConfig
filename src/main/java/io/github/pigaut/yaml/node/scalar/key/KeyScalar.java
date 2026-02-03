package io.github.pigaut.yaml.node.scalar.key;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import org.jetbrains.annotations.*;

import java.util.regex.*;

public class KeyScalar extends Scalar {

    private Section parent;

    public KeyScalar(@NotNull Section parent, @NotNull String key) {
        super(ParseUtil.parseAsScalar(key));
        this.parent = parent;
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        throw new IllegalStateException("Cannot split a key scalar.");
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public @NotNull Branch getParent() throws UnsupportedOperationException {
        return parent;
    }

    @Override
    public @NotNull ConfigRoot getRoot() {
        return parent.getRoot();
    }

    @Override
    public @NotNull String getKey() {
        return toString();
    }

}
