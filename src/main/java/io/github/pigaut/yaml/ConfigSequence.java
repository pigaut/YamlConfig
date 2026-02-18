package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public interface ConfigSequence extends ConfigBranch, Iterable<KeylessField> {

    boolean isSet(int index);
    <T> void set(int index, @Nullable T value);
    void remove(int index);

    Stream<KeylessField> stream();
    Set<KeylessField> getNestedFields();
    Set<KeylessScalar> getNestedScalars();
    Set<KeylessSection> getNestedSections();
    Set<KeylessSequence> getNestedSequences();

    @NotNull ConfigSection getSectionOrCreate(int index);
    @NotNull ConfigSequence getSequenceOrCreate(int index);
    @NotNull ConfigScalar getScalarOrCreate(int index);

    @NotNull ConfigSection addEmptySection();
    @NotNull ConfigSequence addEmptySequence();
    @NotNull ConfigScalar addEmptyScalar();

    <T> @NotNull T getRequired(int index, Class<T> classType) throws InvalidConfigException;
    @NotNull ConfigField getRequiredField(int index) throws InvalidConfigException;
    @NotNull ConfigScalar getRequiredScalar(int index) throws InvalidConfigException;
    @NotNull ConfigSection getRequiredSection(int index) throws InvalidConfigException;
    @NotNull ConfigSequence getRequiredSequence(int index) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredLine(int index) throws InvalidConfigException;
    @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigException;
    @NotNull Character getRequiredCharacter(int index) throws InvalidConfigException;
    @NotNull String getRequiredString(int index) throws InvalidConfigException;
    @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigException;
    @NotNull Integer getRequiredInteger(int index) throws InvalidConfigException;
    @NotNull Long getRequiredLong(int index) throws InvalidConfigException;
    @NotNull Float getRequiredFloat(int index) throws InvalidConfigException;
    @NotNull Double getRequiredDouble(int index) throws InvalidConfigException;

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

    <T> ConfigList<T> toList(Class<T> classType);
    ConfigList<ConfigScalar> toScalarList();
    ConfigList<ConfigSection> toSectionList();
    ConfigList<ConfigSequence> toSequenceList();
    ConfigList<Boolean> toBooleanList();
    ConfigList<Character> toCharacterList();
    ConfigList<String> toStringList();
    ConfigList<String> toStringList(StringFormatter formatter);
    ConfigList<Integer> toIntegerList();
    ConfigList<Long> toLongList();
    ConfigList<Float> toFloatList();
    ConfigList<Double> toDoubleList();

}
