package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
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

    Set<? extends ConfigField> getNestedFields(@NotNull String path);
    Set<ConfigSection> getNestedSections(@NotNull String path);
    Set<ConfigSequence> getNestedSequences(@NotNull String path);

    <T> List<T> getAll(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigurationException;
    <T> List<T> getAllOrSkip(@NotNull String path, @NotNull Class<T> classType);

    <T> @NotNull T getRequired(@NotNull String path, @NotNull Class<T> classType) throws InvalidConfigurationException;
    ConfigField getRequiredField(@NotNull String path) throws InvalidConfigurationException;
    ConfigScalar getRequiredScalar(@NotNull String path) throws InvalidConfigurationException;
    ConfigSection getRequiredSection(@NotNull String path) throws InvalidConfigurationException;
    ConfigSequence getRequiredSequence(@NotNull String path) throws InvalidConfigurationException;
    ConfigLine getRequiredLine(@NotNull String path) throws InvalidConfigurationException;
    @NotNull Boolean getRequiredBoolean(@NotNull String path) throws InvalidConfigurationException;
    @NotNull Character getRequiredCharacter(@NotNull String path) throws InvalidConfigurationException;
    @NotNull String getRequiredString(@NotNull String path) throws InvalidConfigurationException;
    @NotNull String getRequiredString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException;
    @NotNull Integer getRequiredInteger(@NotNull String path) throws InvalidConfigurationException;
    @NotNull Long getRequiredLong(@NotNull String path) throws InvalidConfigurationException;
    @NotNull Float getRequiredFloat(@NotNull String path) throws InvalidConfigurationException;
    @NotNull Double getRequiredDouble(@NotNull String path) throws InvalidConfigurationException;

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
