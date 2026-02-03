package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.key.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public class KeyedScalar extends Scalar implements KeyedField {

    private final Section parent;
    private final String key;

    public KeyedScalar(@NotNull Section parent, @NotNull String key, @NotNull Object value) {
        super(value);
        this.parent = parent;
        this.key = key;
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
        return key;
    }

    @Override
    public @NotNull ConfigScalar getKeyAsScalar() {
        return new KeyScalar(parent, key);
    }

    @Override
    public <T> ConfigOptional<T> getKeyAs(Class<T> classType) {
        return getKeyAsScalar().load(classType);
    }

    @Override
    public ConfigOptional<Boolean> getBooleanKey() {
        return getKeyAsScalar().toBoolean();
    }

    @Override
    public ConfigOptional<Character> getCharacterKey() {
        return getKeyAsScalar().toCharacter();
    }

    @Override
    public ConfigOptional<Integer> getIntegerKey() {
        return getKeyAsScalar().toInteger();
    }

    @Override
    public ConfigOptional<Long> getLongKey() {
        return getKeyAsScalar().toLong();
    }

    @Override
    public ConfigOptional<Float> getFloatKey() {
        return getKeyAsScalar().toFloat();
    }

    @Override
    public ConfigOptional<Double> getDoubleKey() {
        return getKeyAsScalar().toDouble();
    }

    @Override
    public @NotNull Section getParent() throws UnsupportedOperationException {
        return parent;
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        final ConfigSequence sequence = new KeyedSequence(parent, key);
        final List<Object> parsedValues = ParseUtil.parseAllAsScalars(pattern.split(toString()));
        sequence.map(parsedValues);
        return sequence;
    }

}
