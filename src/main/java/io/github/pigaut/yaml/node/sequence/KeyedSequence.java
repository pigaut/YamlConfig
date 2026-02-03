package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.key.*;
import io.github.pigaut.yaml.node.section.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

public class KeyedSequence extends Sequence implements KeyedField {

    private final Section parent;
    private final String key;

    public KeyedSequence(@NotNull Section parent, @NotNull String key) {
        super(FlowStyle.BLOCK);
        this.parent = parent;
        this.key = key;
        final FlowStyle defaultStyle = parent.getNestedFlowStyle();
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
    public @NotNull String getKey() throws UnsupportedOperationException {
        return key;
    }

    @Override
    public @NotNull Section getParent() throws UnsupportedOperationException {
        return parent;
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
    public @NotNull Section convertToSection() {
        KeyedSection section = new KeyedSection(parent, key);
        parent.addNode(section);
        return section;
    }

}
