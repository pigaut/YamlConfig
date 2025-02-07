package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.parser.deserializer.*;
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
    public ConfigSequence split(String regex) {
        return new KeylessSequence(parent, index, Deserializers.parseAll(this.toString().split(regex)));
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        Matcher matcher = pattern.matcher(this.toString());
        List<String> parts = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    parts.add(matcher.group(i));
                    break;
                }
            }
        }
        return new KeylessSequence(parent, index, Deserializers.parseAll(parts));
    }

}
