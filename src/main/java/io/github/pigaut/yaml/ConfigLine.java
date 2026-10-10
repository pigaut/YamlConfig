package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.line.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigLine extends ConfigField, Iterable<ConfigScalar> {

    boolean equals(@NotNull String value);
    boolean equalsIgnoreCase(@NotNull String value);
    boolean contains(@NotNull String string);

    @Nullable String getFormat();
    boolean matchesFormat(@NotNull String format);

    int size();
    int valueCount();
    int flagCount();

    int indexOf(@NotNull Object value);

    boolean hasFlag(@NotNull String key);
    @NotNull Map<String, ConfigScalar> getAllFlags();

    @NotNull ConfigScalar toScalar();

    @NotNull String toString();
    @NotNull String toString(@NotNull StringFormatter formatter);

    void set(@NotNull Object value);
    void setValue(int index, @NotNull Object value);
    void setFlag(@NotNull String key, @NotNull Object value);

    <T> ConfigOptional<T> get(int index, @NotNull Class<T> classType);
    <T> T getRequired(int index, @NotNull Class<T> classType) throws InvalidConfigException;

    <T> ConfigOptional<T> get(@NotNull String key, @NotNull Class<T> classType);
    <T> T getRequired(@NotNull String key, @NotNull Class<T> classType) throws InvalidConfigException;

    <T> ConfigList<T> getAll(@NotNull Class<T> classType);
    <T> ConfigList<T> getAll(int startIndex, @NotNull Class<T> classType);

    <T> List<T> getAllRequired(@NotNull Class<T> classType) throws InvalidConfigException;
    <T> List<T> getAllRequired(int startIndex, @NotNull Class<T> classType) throws InvalidConfigException;

    @NotNull ConfigScalar getRequiredScalar(int index) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredSubLine(int index) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredSubLine(int index, @NotNull LineStyle lineStyle) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredSubLine(int index, @NotNull LineStyle lineStyle, @NotNull String format) throws InvalidConfigException;
    @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigException;
    @NotNull Character getRequiredCharacter(int index) throws InvalidConfigException;
    @NotNull String getRequiredString(int index) throws InvalidConfigException;
    @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigException;
    @NotNull Integer getRequiredInteger(int index) throws InvalidConfigException;
    @NotNull Long getRequiredLong(int index) throws InvalidConfigException;
    @NotNull Float getRequiredFloat(int index) throws InvalidConfigException;
    @NotNull Double getRequiredDouble(int index) throws InvalidConfigException;

    ConfigOptional<ConfigScalar> getScalar(int index);
    ConfigOptional<ConfigLine> getSubLine(int index);
    ConfigOptional<ConfigLine> getSubLine(int index, @NotNull LineStyle lineStyle);
    ConfigOptional<ConfigLine> getSubLine(int index, @NotNull LineStyle lineStyle, @NotNull String format);
    ConfigOptional<Boolean> getBoolean(int index);
    ConfigOptional<Character> getCharacter(int index);
    ConfigOptional<String> getString(int index);
    ConfigOptional<String> getString(int index, @NotNull StringFormatter formatter);
    ConfigOptional<Integer> getInteger(int index);
    ConfigOptional<Long> getLong(int index);
    ConfigOptional<Float> getFloat(int index);
    ConfigOptional<Double> getDouble(int index);

    @NotNull ConfigScalar getRequiredScalar(@NotNull String key) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredSubLine(@NotNull String key) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredSubLine(@NotNull String key, @NotNull LineStyle lineStyle) throws InvalidConfigException;
    @NotNull ConfigLine getRequiredSubLine(@NotNull String key, @NotNull LineStyle lineStyle, @NotNull String format) throws InvalidConfigException;
    @NotNull Boolean getRequiredBoolean(@NotNull String key) throws InvalidConfigException;
    @NotNull Character getRequiredCharacter(@NotNull String key) throws InvalidConfigException;
    @NotNull String getRequiredString(@NotNull String key) throws InvalidConfigException;
    @NotNull String getRequiredString(@NotNull String key, @NotNull StringFormatter formatter) throws InvalidConfigException;
    @NotNull Integer getRequiredInteger(@NotNull String key) throws InvalidConfigException;
    @NotNull Long getRequiredLong(@NotNull String key) throws InvalidConfigException;
    @NotNull Float getRequiredFloat(@NotNull String key) throws InvalidConfigException;
    @NotNull Double getRequiredDouble(@NotNull String key) throws InvalidConfigException;

    ConfigOptional<ConfigScalar> getScalar(@NotNull String key);
    ConfigOptional<ConfigLine> getSubLine(@NotNull String key);
    ConfigOptional<ConfigLine> getSubLine(@NotNull String key, @NotNull LineStyle lineStyle);
    ConfigOptional<ConfigLine> getSubLine(@NotNull String key, @NotNull LineStyle lineStyle, @NotNull String format);
    ConfigOptional<Boolean> getBoolean(@NotNull String key);
    ConfigOptional<Character> getCharacter(@NotNull String key);
    ConfigOptional<String> getString(@NotNull String key);
    ConfigOptional<String> getString(@NotNull String key, @NotNull StringFormatter formatter);
    ConfigOptional<Integer> getInteger(@NotNull String key);
    ConfigOptional<Long> getLong(@NotNull String key);
    ConfigOptional<Float> getFloat(@NotNull String key);
    ConfigOptional<Double> getDouble(@NotNull String key);

}
