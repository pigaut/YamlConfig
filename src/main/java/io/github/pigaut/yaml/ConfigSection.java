package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigSection extends ConfigBranch, Iterable<ConfigField> {

    Set<String> getKeys();

    boolean contains(@NotNull String path);

    boolean isSet(@NotNull String path);

    boolean isSection(@NotNull String path);

    boolean isSequence(@NotNull String path);

    <T> void set(@NotNull String path, @NotNull T value);

    void remove(@NotNull String path);

    void formatKeys(StringFormatter formatter);

    <T> ConfigOptional<T> get(@NotNull String path, @NotNull Class<T> type);

    <T> List<T> getAll(@NotNull String path, @NotNull Class<T> type) throws InvalidConfigurationException;

    <T> List<T> getAllOrSkip(@NotNull String path, @NotNull Class<T> type);

    ConfigOptional<ConfigSection> getSection(@NotNull String path);

    ConfigSection getSectionOrCreate(@NotNull String path);

    ConfigOptional<ConfigSequence> getSequence(@NotNull String path);

    ConfigSequence getSequenceOrCreate(@NotNull String path);

    ConfigOptional<ConfigScalar> getScalar(@NotNull String path);

    Set<ConfigField> getNestedFields(@NotNull String path);

    Set<ConfigSection> getNestedSections(@NotNull String path);

    Set<ConfigSequence> getNestedSequences(@NotNull String path);

    ConfigOptional<ConfigField> getField(@NotNull String path);

    ConfigOptional<Boolean> getBoolean(@NotNull String path);

    ConfigOptional<Character> getCharacter(@NotNull String path) throws InvalidConfigurationException;

    ConfigOptional<String> getString(@NotNull String path) throws InvalidConfigurationException;

    ConfigOptional<String> getString(@NotNull String path, @NotNull StringFormatter formatter) throws InvalidConfigurationException;

    ConfigOptional<Integer> getInteger(@NotNull String path) throws InvalidConfigurationException;

    ConfigOptional<Long> getLong(@NotNull String path) throws InvalidConfigurationException;

    ConfigOptional<Float> getFloat(@NotNull String path) throws InvalidConfigurationException;

    ConfigOptional<Double> getDouble(@NotNull String path) throws InvalidConfigurationException;

    List<ConfigField> getFieldList(@NotNull String path);

    List<ConfigScalar> getScalarList(@NotNull String path);

    List<ConfigSection> getSectionList(@NotNull String path);

    List<Boolean> getBooleanList(@NotNull String path);

    List<Character> getCharacterList(@NotNull String path);

    List<String> getStringList(@NotNull String path);

    List<String> getStringList(@NotNull String path, @NotNull StringFormatter formatter);

    List<Integer> getIntegerList(@NotNull String path);

    List<Long> getLongList(@NotNull String path);

    List<Float> getFloatList(@NotNull String path);

    List<Double> getDoubleList(@NotNull String path);

}
