package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public interface ConfigBranch extends ConfigField, Iterable<ConfigField> {

    int size();

    boolean isEmpty();

    @NotNull
    FlowStyle getFlowStyle();

    void setFlowStyle(@NotNull FlowStyle flowStyle);

    @Nullable
    FlowStyle getNestedFlowStyle();

    void setNestedFlowStyle(@Nullable FlowStyle flowStyle);

    @Nullable
    ScalarStyle getNestedScalarStyle();

    void setNestedScalarStyle(@Nullable ScalarStyle scalarStyle);

    Stream<ConfigField> stream();

    Set<ConfigField> getNestedFields();

    Set<ConfigSection> getNestedSections() throws InvalidConfigurationException;

    Set<ConfigSequence> getNestedSequences() throws InvalidConfigurationException;

    @NotNull
    ConfigSection convertToSection();

    @NotNull
    ConfigSequence convertToSequence();

    <T> void map(T value);

    <T> void add(T value);

    <T> void addAll(Collection<T> elements);

    <T> List<@NotNull T> getAll(@NotNull Class<T> type) throws InvalidConfigurationException;

    <T> List<T> getAllOrSkip(@NotNull Class<T> type);

}
