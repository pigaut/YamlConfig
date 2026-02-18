package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigLine extends ConfigField, Iterable<ConfigScalar> {

    boolean contains(@NotNull String value);
    boolean hasFlag(@NotNull String key);

    @NotNull ConfigScalar asScalar();

    @NotNull String getValue();
    void setValue(@NotNull String line) throws InvalidConfigException;

    void set(int index, Object value);
    void setFlag(@NotNull String key, Object value);

    <T> ConfigOptional<T> get(int index, @NotNull Class<T> classType);
    <T> T getRequired(int index, @NotNull Class<T> classType) throws InvalidConfigException;

    <T> ConfigOptional<T> get(@NotNull String key, @NotNull Class<T> classType);
    <T> T getRequired(@NotNull String key, @NotNull Class<T> classType) throws InvalidConfigException;

    <T> ConfigList<T> getAll(@NotNull Class<T> classType);
    <T> ConfigList<T> getAll(int startIndex, @NotNull Class<T> classType);

    <T> List<T> getAllRequired(@NotNull Class<T> classType) throws InvalidConfigException;
    <T> List<T> getAllRequired(int startIndex, @NotNull Class<T> classType) throws InvalidConfigException;

    @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigException;
    @NotNull Character getRequiredCharacter(int index) throws InvalidConfigException;
    @NotNull String getRequiredString(int index) throws InvalidConfigException;
    @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigException;
    @NotNull Integer getRequiredInteger(int index) throws InvalidConfigException;
    @NotNull Long getRequiredLong(int index) throws InvalidConfigException;
    @NotNull Float getRequiredFloat(int index) throws InvalidConfigException;
    @NotNull Double getRequiredDouble(int index) throws InvalidConfigException;

    ConfigOptional<Boolean> getBoolean(int index);
    ConfigOptional<Character> getCharacter(int index);
    ConfigOptional<String> getString(int index);
    ConfigOptional<String> getString(int index, @NotNull StringFormatter formatter);
    ConfigOptional<Integer> getInteger(int index);
    ConfigOptional<Long> getLong(int index);
    ConfigOptional<Float> getFloat(int index);
    ConfigOptional<Double> getDouble(int index);

    @NotNull Boolean getRequiredBoolean(@NotNull String key) throws InvalidConfigException;
    @NotNull Character getRequiredCharacter(@NotNull String key) throws InvalidConfigException;
    @NotNull String getRequiredString(@NotNull String key) throws InvalidConfigException;
    @NotNull String getRequiredString(@NotNull String key, @NotNull StringFormatter formatter) throws InvalidConfigException;
    @NotNull Integer getRequiredInteger(@NotNull String key) throws InvalidConfigException;
    @NotNull Long getRequiredLong(@NotNull String key) throws InvalidConfigException;
    @NotNull Float getRequiredFloat(@NotNull String key) throws InvalidConfigException;
    @NotNull Double getRequiredDouble(@NotNull String key) throws InvalidConfigException;

    ConfigOptional<Boolean> getBoolean(@NotNull String key);
    ConfigOptional<Character> getCharacter(@NotNull String key);
    ConfigOptional<String> getString(@NotNull String key);
    ConfigOptional<String> getString(@NotNull String key, @NotNull StringFormatter formatter);
    ConfigOptional<Integer> getInteger(@NotNull String key);
    ConfigOptional<Long> getLong(@NotNull String key);
    ConfigOptional<Float> getFloat(@NotNull String key);
    ConfigOptional<Double> getDouble(@NotNull String key);

}
