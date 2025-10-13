package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.optional.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigLine extends ConfigField, Iterable<ConfigScalar> {

    boolean contains(@NotNull String value);
    boolean hasFlag(@NotNull String key);

    @NotNull ConfigScalar asScalar();

    @NotNull String getValue();
    void setValue(@NotNull String line) throws InvalidConfigurationException;

    void set(int index, Object value);
    void setFlag(@NotNull String key, Object value);

    <T> ConfigOptional<T> get(int index, Class<T> classType);
    <T> T getRequired(int index, Class<T> classType) throws InvalidConfigurationException;

    <T> ConfigOptional<T> get(String key, Class<T> classType);
    <T> T getRequired(String key, Class<T> classType) throws InvalidConfigurationException;

    <T> List<T> getAll(Class<T> classType) throws InvalidConfigurationException;
    <T> List<T> getAllOrSkip(Class<T> classType);

    <T> List<T> getAll(int startIndex, Class<T> classType) throws InvalidConfigurationException;
    <T> List<T> getAllOrSkip(int startIndex, Class<T> classType);

    @NotNull Boolean getRequiredBoolean(int index) throws InvalidConfigurationException;
    @NotNull Character getRequiredCharacter(int index) throws InvalidConfigurationException;
    @NotNull String getRequiredString(int index) throws InvalidConfigurationException;
    @NotNull String getRequiredString(int index, @NotNull StringFormatter formatter) throws InvalidConfigurationException;
    @NotNull Integer getRequiredInteger(int index) throws InvalidConfigurationException;
    @NotNull Long getRequiredLong(int index) throws InvalidConfigurationException;
    @NotNull Float getRequiredFloat(int index) throws InvalidConfigurationException;
    @NotNull Double getRequiredDouble(int index) throws InvalidConfigurationException;

    ConfigOptional<Boolean> getBoolean(int index);
    ConfigOptional<Character> getCharacter(int index);
    ConfigOptional<String> getString(int index);
    ConfigOptional<String> getString(int index, @NotNull StringFormatter formatter);
    ConfigOptional<Integer> getInteger(int index);
    ConfigOptional<Long> getLong(int index);
    ConfigOptional<Float> getFloat(int index);
    ConfigOptional<Double> getDouble(int index);

    @NotNull Boolean getRequiredBoolean(@NotNull String key) throws InvalidConfigurationException;
    @NotNull Character getRequiredCharacter(@NotNull String key) throws InvalidConfigurationException;
    @NotNull String getRequiredString(@NotNull String key) throws InvalidConfigurationException;
    @NotNull String getRequiredString(@NotNull String key, @NotNull StringFormatter formatter) throws InvalidConfigurationException;
    @NotNull Integer getRequiredInteger(@NotNull String key) throws InvalidConfigurationException;
    @NotNull Long getRequiredLong(@NotNull String key) throws InvalidConfigurationException;
    @NotNull Float getRequiredFloat(@NotNull String key) throws InvalidConfigurationException;
    @NotNull Double getRequiredDouble(@NotNull String key) throws InvalidConfigurationException;

    ConfigOptional<Boolean> getBoolean(@NotNull String key);
    ConfigOptional<Character> getCharacter(@NotNull String key);
    ConfigOptional<String> getString(@NotNull String key);
    ConfigOptional<String> getString(@NotNull String key, @NotNull StringFormatter formatter);
    ConfigOptional<Integer> getInteger(@NotNull String key);
    ConfigOptional<Long> getLong(@NotNull String key);
    ConfigOptional<Float> getFloat(@NotNull String key);
    ConfigOptional<Double> getDouble(@NotNull String key);

}
