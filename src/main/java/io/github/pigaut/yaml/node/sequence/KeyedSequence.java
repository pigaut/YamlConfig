package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.key.*;
import io.github.pigaut.yaml.node.section.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

public class KeyedSequence extends Sequence implements KeyedField {

    private final Section parent;
    private final ScalarKey key;

    public KeyedSequence(@NotNull Section parent, @NotNull String key) {
        super(FlowStyle.BLOCK);
        this.parent = parent;
        this.key = new ScalarKey(parent, this, key);
        FlowStyle defaultStyle = parent.getNestedFlowStyle();
        if (defaultStyle != null) {
            setFlowStyle(defaultStyle);
        }
    }

    public KeyedSequence(@NotNull Section parent, @NotNull ScalarKey key) {
        super(FlowStyle.BLOCK);
        this.parent = parent;
        this.key = key;
        FlowStyle defaultStyle = parent.getNestedFlowStyle();
        if (defaultStyle != null) {
            setFlowStyle(defaultStyle);
        }
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
    public @NotNull Section getParent() {
        return parent;
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
    public @NotNull Section convertToSection() {
        KeyedSection section = new KeyedSection(parent, key);
        parent.addNode(section);
        return section;
    }

}
