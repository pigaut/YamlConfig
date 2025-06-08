package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.parser.deserializer.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

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

    @Override
    public ConfigSequence split(String regex) {
        final ConfigSequence sequence = new KeyedSequence(parent, key);
        final List<Object> parsedValues = Deserializers.parseAll(this.toString().split(regex));
        sequence.map(parsedValues);
        return sequence;
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

        final ConfigSequence sequence = new KeyedSequence(parent, key);
        final List<Object> parsedValues = Deserializers.parseAll(parts);
        sequence.map(parsedValues);
        return sequence;
    }

}
