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

    @NotNull
    ConfigSection convertToSection();

    @NotNull
    ConfigSequence convertToSequence();

    <T> void map(T value);

    <T> void add(T value);

    <T> void addAll(Collection<T> elements);

    <T> List<@NotNull T> getAll(@NotNull Class<T> type) throws InvalidConfigurationException;

    ConfigField getField(String path, int index);

    ConfigScalar getScalar(String path, int index) throws InvalidConfigurationException;

    Optional<ConfigScalar> getOptionalScalar(String path, int index);

    ConfigSection getSection(String path, int index) throws InvalidConfigurationException;

    Optional<ConfigSection> getOptionalSection(String path, int index);

    ConfigSection getSectionOrCreate(String path, int index);

    ConfigSequence getSequence(String path, int index) throws InvalidConfigurationException;

    Optional<ConfigSequence> getOptionalSequence(String path, int index);

    ConfigSequence getSequenceOrCreate(String path, int index);

    boolean getBoolean(String path, int index) throws InvalidConfigurationException;

    char getCharacter(String path, int index) throws InvalidConfigurationException;

    @NotNull
    String getString(String path, int index) throws InvalidConfigurationException;

    int getInteger(String path, int index) throws InvalidConfigurationException;

    long getLong(String path, int index) throws InvalidConfigurationException;

    float getFloat(String path, int index) throws InvalidConfigurationException;

    double getDouble(String path, int index) throws InvalidConfigurationException;

    Optional<Boolean> getOptionalBoolean(String path, int index);

    Optional<Character> getOptionalCharacter(String path, int index);

    Optional<String> getOptionalString(String path, int index);

    Optional<Integer> getOptionalInteger(String path, int index);

    Optional<Long> getOptionalLong(String path, int index);

    Optional<Float> getOptionalFloat(String path, int index);

    Optional<Double> getOptionalDouble(String path, int index);

}
