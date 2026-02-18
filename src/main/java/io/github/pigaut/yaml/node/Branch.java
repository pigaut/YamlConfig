package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
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
        for (ConfigField field : stream().toList()) {
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
        for (ConfigField field : stream().toList()) {
            if (field instanceof ConfigScalar scalar) {
                scalar.setScalarStyle(scalarStyle);
            }
        }
        this.nestedScalarStyle = scalarStyle;
    }

//    @Override
//    public Set<ConfigScalar> getNestedScalars() {
//        return stream()
//                .map(field -> field.toScalar().orElse(null))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toCollection(LinkedHashSet::new));
//    }
//
//    @Override
//    public Set<ConfigSection> getNestedSections() {
//        return stream()
//                .map(field -> field.toSection().orElse(null))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toCollection(LinkedHashSet::new));
//    }
//
//    @Override
//    public Set<ConfigSequence> getNestedSequences() {
//        return stream()
//                .map(field -> field.toSequence().orElse(null))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toCollection(LinkedHashSet::new));
//    }

    @NotNull
    public abstract Section convertToSection();

    @NotNull
    public abstract Sequence convertToSequence();

    @Override
    public abstract <T> void map(T value);

    @Override
    public abstract <T> void add(@NotNull T value);

    @Override
    public abstract <T> void addAll(Collection<T> elements);

    @Override
    public <T> ConfigList<T> getAll(@NotNull Class<T> classType) {
        List<T> elements = new ArrayList<>();
        for (ConfigField nestedField : getNestedFields()) {
            try {
                elements.add(nestedField.getRequired(classType));
            } catch (InvalidConfigException e) {
                return ConfigList.invalid(e);
            }
        }
        return ConfigList.of(this, elements);
    }

    @Override
    public <T> List<T> getAllRequired(@NotNull Class<T> classType) throws InvalidConfigException {
        List<T> elements = new ArrayList<>();
        for (ConfigField nestedField : getNestedFields()) {
            elements.add(nestedField.getRequired(classType));
        }
        return elements;
    }

    public abstract @NotNull List<@NotNull Object> toList();

    public abstract @NotNull Map<@NotNull String, @NotNull Object> toMap();
}
