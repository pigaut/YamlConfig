package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public abstract class Branch extends Field implements ConfigBranch {

    private @NotNull FlowStyle flowStyle;
    private @Nullable FlowStyle nestedFlowStyle = null;
    private @Nullable ScalarStyle nestedScalarStyle = null;

    protected Branch(@NotNull FlowStyle flowStyle) {
        this.flowStyle = flowStyle;
    }

    @Override
    public abstract int size();

    @Override
    public abstract boolean isEmpty();

    @Override
    public abstract Stream<ConfigField> stream();

    @Override
    public abstract <T> void map(T value);

    @Override
    public abstract <T> void add(T value);

    @Override
    public abstract <T> void addAll(Collection<T> elements);

    @NotNull
    public abstract Section convertToSection();

    @NotNull
    public abstract Sequence convertToSequence();

    public @NotNull FlowStyle getFlowStyle() {
        return flowStyle;
    }

    public void setFlowStyle(@NotNull FlowStyle flowStyle) {
        this.flowStyle = flowStyle;
    }

    @Override
    public @Nullable FlowStyle getNestedFlowStyle() {
        return nestedFlowStyle;
    }

    @Override
    public void setNestedFlowStyle(@Nullable FlowStyle flowStyle) {
        for (ConfigField field : this) {
            if (field instanceof ConfigBranch branch) {
                branch.setFlowStyle(flowStyle);
            }
        }
        this.nestedFlowStyle = flowStyle;
    }

    @Override
    public @Nullable ScalarStyle getNestedScalarStyle() {
        return nestedScalarStyle;
    }

    @Override
    public void setNestedScalarStyle(@Nullable ScalarStyle scalarStyle) {
        for (ConfigField field : this) {
            if (field instanceof ConfigScalar scalar) {
                scalar.setScalarStyle(scalarStyle);
            }
        }
        this.nestedScalarStyle = scalarStyle;
    }

    @Override
    public Set<@NotNull ConfigField> getNestedFields() {
        return this.stream()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<@NotNull ConfigSection> getNestedSections() throws InvalidConfigurationException {
        return this.stream()
                .map(ConfigField::toSection)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public <T> List<T> getAll(@NotNull Class<T> type) throws InvalidConfigurationException {
        final List<T> elements = new ArrayList<>();
        for (ConfigField nestedField : this) {
            elements.add(nestedField.load(type));
        }
        return elements;
    }

    @Override
    public ConfigField getField(String path, int index) {
        if (this instanceof ConfigSection section) {
            return section.getField(path);
        } else {
            return ((ConfigSequence) this).getField(index);
        }
    }

    @Override
    public ConfigScalar getScalar(String path, int index) throws InvalidConfigurationException {
        return getField(path, index).toScalar();
    }

    @Override
    public Optional<ConfigScalar> getOptionalScalar(String path, int index) {
        return ConfigOptional.of(() -> getScalar(path, index));
    }

    @Override
    public ConfigSection getSection(String path, int index) throws InvalidConfigurationException {
        return getField(path, index).toSection();
    }

    @Override
    public Optional<ConfigSection> getOptionalSection(String path, int index) {
        return ConfigOptional.of(() -> getSection(path, index));
    }

    @Override
    public ConfigSection getSectionOrCreate(String path, int index) {
        if (this instanceof ConfigSection section) {
            return section.getSectionOrCreate(path);
        }
        else {
            return ((ConfigSequence) this).getSectionOrCreate(index);
        }
    }

    @Override
    public ConfigSequence getSequence(String path, int index) throws InvalidConfigurationException {
        return getField(path, index).toSequence();
    }

    @Override
    public Optional<ConfigSequence> getOptionalSequence(String path, int index) {
        return ConfigOptional.of(() -> getSequence(path, index));
    }

    @Override
    public ConfigSequence getSequenceOrCreate(String path, int index) {
        if (this instanceof ConfigSection section) {
            return section.getSequenceOrCreate(path);
        } else {
            return ((ConfigSequence) this).getSequenceOrCreate(index);
        }
    }

    @Override
    public boolean getBoolean(String path, int index) throws InvalidConfigurationException {
        return getScalar(path, index).toBoolean();
    }

    @Override
    public char getCharacter(String path, int index) throws InvalidConfigurationException {
        return getScalar(path, index).toCharacter();
    }

    @Override
    public @NotNull String getString(String path, int index) throws InvalidConfigurationException {
        return getScalar(path, index).toString();
    }

    @Override
    public int getInteger(String path, int index) throws InvalidConfigurationException {
        return getScalar(path, index).toInteger();
    }

    @Override
    public long getLong(String path, int index) throws InvalidConfigurationException {
        return getScalar(path, index).toLong();
    }

    @Override
    public float getFloat(String path, int index) throws InvalidConfigurationException {
        return getScalar(path, index).toFloat();
    }

    @Override
    public double getDouble(String path, int index) throws InvalidConfigurationException {
        return getScalar(path, index).toDouble();
    }

    @Override
    public Optional<Boolean> getOptionalBoolean(String path, int index) {
        return ConfigOptional.of(() -> getBoolean(path, index));
    }

    @Override
    public Optional<Character> getOptionalCharacter(String path, int index) {
        return ConfigOptional.of(() -> getCharacter(path, index));
    }

    @Override
    public Optional<String> getOptionalString(String path, int index) {
        return ConfigOptional.of(() -> getString(path, index));
    }

    @Override
    public Optional<Integer> getOptionalInteger(String path, int index) {
        return ConfigOptional.of(() -> getInteger(path, index));
    }

    @Override
    public Optional<Long> getOptionalLong(String path, int index) {
        return ConfigOptional.of(() -> getLong(path, index));
    }

    @Override
    public Optional<Float> getOptionalFloat(String path, int index) {
        return ConfigOptional.of(() -> getFloat(path, index));
    }

    @Override
    public Optional<Double> getOptionalDouble(String path, int index) {
        return ConfigOptional.of(() -> getDouble(path, index));
    }

    public abstract @NotNull List<@NotNull Object> toList();

    public abstract @NotNull Map<@NotNull String, @NotNull Object> toMap();
}
