package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public interface ConfigSection extends ConfigBranch, Iterable<KeyedField> {

    Set<String> getKeys();
    boolean contains(@NotNull String path);
    boolean isSet(@NotNull String path);
    boolean isSection(@NotNull String path);
    boolean isSequence(@NotNull String path);
    <T> void set(@NotNull String path, @NotNull T value);
    void remove(@NotNull String path);

    ConfigSection getSectionOrCreate(@NotNull String path);
    ConfigSequence getSequenceOrCreate(@NotNull String path);
    ConfigScalar getScalarOrCreate(@NotNull String path);

    Stream<KeyedField> stream();
    Set<KeyedField> getNestedFields();
    Set<KeyedScalar> getNestedScalars();
    Set<KeyedSection> getNestedSections();
    Set<KeyedSequence> getNestedSequences();
    
    <T> ConfigList<T> getAll(@NotNull String path, @NotNull Class<T> classType);
    <T> List<T> getAllRequired(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigException;

    <T> @NotNull T getRequired(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigException;
    @NotNull ConfigField getRequiredField(@NotNull String path) throws InvalidConfigException;
    @NotNull ConfigScalar getRequiredScalar(@NotNull String path) throws InvalidConfigException;
    @NotNull ConfigSection getRequiredSection(@NotNull String path) throws InvalidConfigException;
    @NotNull ConfigSequence getRequiredSequence(@NotNull String path) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredLine(@NotNull String path) throws InvalidConfigException;
    @NotNull Boolean getRequiredBoolean(@NotNull String path) throws InvalidConfigException;
    @NotNull Character getRequiredCharacter(@NotNull String path) throws InvalidConfigException;
    @NotNull String getRequiredString(@NotNull String path) throws InvalidConfigException;
    @NotNull String getRequiredString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigException;
    @NotNull Integer getRequiredInteger(@NotNull String path) throws InvalidConfigException;
    @NotNull Long getRequiredLong(@NotNull String path) throws InvalidConfigException;
    @NotNull Float getRequiredFloat(@NotNull String path) throws InvalidConfigException;
    @NotNull Double getRequiredDouble(@NotNull String path) throws InvalidConfigException;

    <T> ConfigOptional<T> get(@NotNull String path, @NotNull Class<T> classType);
    ConfigOptional<ConfigField> getField(@NotNull String path);
    ConfigOptional<ConfigScalar> getScalar(@NotNull String path);
    ConfigOptional<ConfigSection> getSection(@NotNull String path);
    ConfigOptional<ConfigSequence> getSequence(@NotNull String path);
    ConfigOptional<ConfigLine> getLine(@NotNull String path);
    ConfigOptional<Boolean> getBoolean(@NotNull String path);
    ConfigOptional<Character> getCharacter(@NotNull String path);
    ConfigOptional<String> getString(@NotNull String path);
    ConfigOptional<String> getString(@NotNull String path, @NotNull StringFormatter formatter);
    ConfigOptional<Integer> getInteger(@NotNull String path);
    ConfigOptional<Long> getLong(@NotNull String path);
    ConfigOptional<Float> getFloat(@NotNull String path);
    ConfigOptional<Double> getDouble(@NotNull String path);

    <T> ConfigList<T> getList(@NotNull String path, Class<T> classType);
    ConfigList<ConfigField> getFieldList(@NotNull String path);
    ConfigList<ConfigScalar> getScalarList(@NotNull String path);
    ConfigList<ConfigSection> getSectionList(@NotNull String path);
    ConfigList<Boolean> getBooleanList(@NotNull String path);
    ConfigList<Character> getCharacterList(@NotNull String path);
    ConfigList<String> getStringList(@NotNull String path);
    ConfigList<String> getStringList(@NotNull String path, @NotNull StringFormatter formatter);
    ConfigList<Integer> getIntegerList(@NotNull String path);
    ConfigList<Long> getLongList(@NotNull String path);
    ConfigList<Float> getFloatList(@NotNull String path);
    ConfigList<Double> getDoubleList(@NotNull String path);

}
