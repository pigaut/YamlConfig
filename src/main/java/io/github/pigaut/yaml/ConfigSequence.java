package io.github.pigaut.yaml;

import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigSequence extends ConfigBranch {

    <T> void set(int index, T value);
    void remove(int index);

    <T> T get(int index, Class<T> type);
    <T> Optional<T> getOptional(int index, Class<T> type);

    ConfigField getField(int index);
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
    @NotNull String getString(int index) throws InvalidConfigurationException;
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
