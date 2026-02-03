package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public interface ConfigBranch extends ConfigField {

    int size();
    boolean isEmpty();
    @NotNull FlowStyle getFlowStyle();
    @Nullable FlowStyle getNestedFlowStyle();
    @Nullable ScalarStyle getNestedScalarStyle();

    void setFlowStyle(@NotNull FlowStyle flowStyle);
    void setNestedFlowStyle(@Nullable FlowStyle flowStyle);
    void setNestedScalarStyle(@Nullable ScalarStyle scalarStyle);

    Stream<? extends ConfigField> stream();

    Set<? extends ConfigField> getNestedFields();
    Set<ConfigScalar> getNestedScalars();
    Set<ConfigSection> getNestedSections();
    Set<ConfigSequence> getNestedSequences();

    @NotNull ConfigSection convertToSection();
    @NotNull ConfigSequence convertToSequence();

    <T> void add(@NotNull T value);
    <T> void addAll(Collection<T> elements);
    <T> List<@NotNull T> getAll(@NotNull Class<T> classType) throws InvalidConfigurationException;
    <T> List<T> getAllOrSkip(@NotNull Class<T> classType);

}
