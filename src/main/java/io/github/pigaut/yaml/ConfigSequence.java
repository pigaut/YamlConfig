package io.github.pigaut.yaml;

import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.stream.*;

public interface ConfigSequence extends ConfigField, Iterable<ConfigField> {

    int size();

    boolean isEmpty();

    boolean isSet(int index);

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

    <T> void set(int index, T value);

    void remove(int index);

    <T> T get(int index, Class<T> type);

    <T> Optional<T> getOptional(int index, Class<T> type);

    ConfigField getField(int index) throws InvalidConfigurationException;

    Optional<ConfigField> getOptionalField(int index);

    ConfigScalar getScalar(int index) throws InvalidConfigurationException;

    Optional<ConfigScalar> getOptionalScalar(int index);

    ConfigSection getSection(int index) throws InvalidConfigurationException;

    Optional<ConfigSection> getOptionalSection(int index);

    ConfigSection getSectionOrCreate(int index);

    ConfigSection addSection();

    ConfigSequence getSequence(int index) throws InvalidConfigurationException;

    Optional<ConfigSequence> getOptionalSequence(int index);

    ConfigSequence getSequenceOrCreate(int index);

    ConfigSequence addSequence();

    boolean getBoolean(int index) throws InvalidConfigurationException;

    char getCharacter(int index) throws InvalidConfigurationException;

    @NotNull
    String getString(int index) throws InvalidConfigurationException;

    int getInteger(int index) throws InvalidConfigurationException;

    long getLong(int index) throws InvalidConfigurationException;

    float getFloat(int index) throws InvalidConfigurationException;

    double getDouble(int index) throws InvalidConfigurationException;

    Optional<Boolean> getOptionalBoolean(int index);

    Optional<Character> getOptionalCharacter(int index);

    Optional<String> getOptionalString(int index);

    Optional<Integer> getOptionalInteger(int index);

    Optional<Long> getOptionalLong(int index);

    Optional<Float> getOptionalFloat(int index);

    Optional<Double> getOptionalDouble(int index);

    <T> @NotNull List<@NotNull T> toList(Class<T> type);

    List<ConfigField> toFieldList();

    List<ConfigScalar> toScalarList() throws InvalidConfigurationException;

    List<ConfigSection> toSectionList() throws InvalidConfigurationException;

    List<ConfigSequence> toSequenceList() throws InvalidConfigurationException;

    List<Boolean> toBooleanList() throws InvalidConfigurationException;

    List<Character> toCharacterList() throws InvalidConfigurationException;

    List<String> toStringList() throws InvalidConfigurationException;

    List<String> toStringList(StringFormatter formatter) throws InvalidConfigurationException;

    List<Integer> toIntegerList() throws InvalidConfigurationException;

    List<Long> toLongList() throws InvalidConfigurationException;

    List<Float> toFloatList() throws InvalidConfigurationException;

    List<Double> toDoubleList() throws InvalidConfigurationException;

}
