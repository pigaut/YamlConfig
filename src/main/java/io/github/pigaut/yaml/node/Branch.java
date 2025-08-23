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
    public abstract Stream<ConfigField> stream();

    @Override
    public Set<ConfigField> getNestedFields() {
        return stream()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConfigSection> getNestedSections() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toSection().orElseThrow())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConfigSequence> getNestedSequences() throws InvalidConfigurationException {
        return stream()
                .map(field -> field.toSequence().orElseThrow())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @NotNull
    public abstract Section convertToSection();

    @NotNull
    public abstract Sequence convertToSequence();

    @Override
    public abstract <T> void map(T value);

    @Override
    public abstract <T> void add(T value);

    @Override
    public abstract <T> void addAll(Collection<T> elements);

    @Override
    public <T> List<T> getAll(@NotNull Class<T> type) throws InvalidConfigurationException {
        final List<T> elements = new ArrayList<>();
        for (ConfigField nestedField : this) {
            elements.add(nestedField.load(type).orElseThrow());
        }
        return elements;
    }

    @Override
    public <T> List<T> getAllOrSkip(@NotNull Class<T> classType) {
        final List<T> elements = new ArrayList<>();
        for (ConfigField nestedField : this) {
            nestedField.load(classType).ifPresent(elements::add);
        }
        return elements;
    }

    @Override
    public ConfigOptional<ConfigBranch> toBranch() {
        return ConfigOptional.of(this);
    }

    public abstract @NotNull List<@NotNull Object> toList();

    public abstract @NotNull Map<@NotNull String, @NotNull Object> toMap();
}
