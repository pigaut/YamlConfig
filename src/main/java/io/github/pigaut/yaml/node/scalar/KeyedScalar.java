package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.key.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.*;
import java.util.regex.*;

public class KeyedScalar extends Scalar implements KeyedField {

    private final Section parent;
    private final ScalarKey key;

    public KeyedScalar(@NotNull Section parent, @NotNull String key, @NotNull Object value) {
        super(value);
        this.parent = parent;
        this.key = new ScalarKey(parent, this, key);
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
    public @NotNull String getKey() {
        return key.getKey();
    }

    @Override
    public @NotNull ConfigScalar getKeyScalar() {
        return key;
    }

    @Override
    public <T> ConfigOptional<T> getKeyAs(Class<T> classType) {
        return key.get(classType);
    }

    @Override
    public ConfigOptional<Boolean> getBooleanKey() {
        return key.toBoolean();
    }

    @Override
    public ConfigOptional<Character> getCharacterKey() {
        return key.toCharacter();
    }

    @Override
    public ConfigOptional<Integer> getIntegerKey() {
        return key.toInteger();
    }

    @Override
    public ConfigOptional<Long> getLongKey() {
        return key.toLong();
    }

    @Override
    public ConfigOptional<Float> getFloatKey() {
        return key.toFloat();
    }

    @Override
    public ConfigOptional<Double> getDoubleKey() {
        return key.toDouble();
    }

    @Override
    public @NotNull Section getParent() {
        return parent;
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        ConfigSequence sequence = new KeyedSequence(parent, key);
        List<Object> parsedValues = ParseUtil.parseAllAsScalars(Tag.STR, pattern.split(toString()));
        sequence.map(parsedValues);
        return sequence;
    }

}
