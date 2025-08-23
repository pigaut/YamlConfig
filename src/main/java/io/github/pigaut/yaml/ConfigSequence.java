package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ConfigSequence extends ConfigBranch, Iterable<ConfigField> {

    boolean isSet(int index);

    <T> void set(int index, @Nullable T value);

    void remove(int index);

    <T> ConfigOptional<T> get(int index, Class<T> type);

    ConfigOptional<ConfigField> getField(int index);

    ConfigOptional<ConfigScalar> getScalar(int index);

    ConfigOptional<ConfigSection> getSection(int index);

    ConfigSection getSectionOrCreate(int index);

    ConfigSection addSection();

    ConfigOptional<ConfigSequence> getSequence(int index);

    ConfigSequence getSequenceOrCreate(int index);

    ConfigSequence addSequence();

    ConfigOptional<Boolean> getBoolean(int index);

    ConfigOptional<Character> getCharacter(int index);

    ConfigOptional<String> getString(int index);

    ConfigOptional<String> getString(int index, StringFormatter formatter);

    ConfigOptional<Integer> getInteger(int index);

    ConfigOptional<Long> getLong(int index);

    ConfigOptional<Float> getFloat(int index);

    ConfigOptional<Double> getDouble(int index);

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
