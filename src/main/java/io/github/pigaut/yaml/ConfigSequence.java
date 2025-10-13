package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.optional.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigSequence extends ConfigBranch, Iterable<ConfigField> {

    boolean isSet(int index);
    <T> void set(int index, @Nullable T value);
    void remove(int index);

    @NotNull ConfigSection getSectionOrCreate(int index);
    @NotNull ConfigSequence getSequenceOrCreate(int index);
    @NotNull ConfigScalar getScalarOrCreate(int index);

    @NotNull ConfigSection addEmptySection();
    @NotNull ConfigSequence addEmptySequence();
    @NotNull ConfigScalar addEmptyScalar();

    <T> @NotNull T getRequired(int index, Class<T> classType) throws InvalidConfigurationException;
    @NotNull ConfigField getRequiredField(int index) throws InvalidConfigurationException;
    @NotNull ConfigScalar getRequiredScalar(int index) throws InvalidConfigurationException;
    @NotNull ConfigSection getRequiredSection(int index) throws InvalidConfigurationException;
    @NotNull ConfigSequence getRequiredSequence(int index) throws InvalidConfigurationException;
    @NotNull ConfigLine getRequiredLine(int index) throws InvalidConfigurationException;
    @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigurationException;
    @NotNull Character getRequiredCharacter(int index) throws InvalidConfigurationException;
    @NotNull String getRequiredString(int index) throws InvalidConfigurationException;
    @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigurationException;
    @NotNull Integer getRequiredInteger(int index) throws InvalidConfigurationException;
    @NotNull Long getRequiredLong(int index) throws InvalidConfigurationException;
    @NotNull Float getRequiredFloat(int index) throws InvalidConfigurationException;
    @NotNull Double getRequiredDouble(int index) throws InvalidConfigurationException;

    <T> ConfigOptional<T> get(int index, Class<T> classType);
    ConfigOptional<ConfigField> getField(int index);
    ConfigOptional<ConfigScalar> getScalar(int index);
    ConfigOptional<ConfigSection> getSection(int index);
    ConfigOptional<ConfigSequence> getSequence(int index);
    ConfigOptional<ConfigLine> getLine(int index);
    ConfigOptional<Boolean> getBoolean(int index);
    ConfigOptional<Character> getCharacter(int index);
    ConfigOptional<String> getString(int index);
    ConfigOptional<String> getString(int index, StringFormatter formatter);
    ConfigOptional<Integer> getInteger(int index);
    ConfigOptional<Long> getLong(int index);
    ConfigOptional<Float> getFloat(int index);
    ConfigOptional<Double> getDouble(int index);

    List<ConfigField> toFieldList();

    <T> ConfigOptional<List<T>> toList(Class<T> classType);
    ConfigOptional<List<ConfigScalar>> toScalarList();
    ConfigOptional<List<ConfigSection>> toSectionList();
    ConfigOptional<List<ConfigSequence>> toSequenceList();
    ConfigOptional<List<Boolean>> toBooleanList();
    ConfigOptional<List<Character>> toCharacterList();
    ConfigOptional<List<String>> toStringList();
    ConfigOptional<List<String>> toStringList(StringFormatter formatter);
    ConfigOptional<List<Integer>> toIntegerList();
    ConfigOptional<List<Long>> toLongList();
    ConfigOptional<List<Float>> toFloatList();
    ConfigOptional<List<Double>> toDoubleList();

}
