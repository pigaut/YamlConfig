package io.github.pigaut.yaml;

import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;

public interface ConfigScalar extends ConfigField {

    @NotNull Object getValue();
    void setValue(@NotNull Object value);

    @NotNull ScalarStyle getScalarStyle();
    void setScalarStyle(@NotNull ScalarStyle scalarStyle);

    boolean toBoolean() throws InvalidConfigurationException;
    char toCharacter() throws InvalidConfigurationException;
    @NotNull String toString();
    @NotNull String toString(@NotNull StringFormatter formatter);
    int toInteger() throws InvalidConfigurationException;
    long toLong() throws InvalidConfigurationException;
    float toFloat() throws InvalidConfigurationException;
    double toDouble() throws InvalidConfigurationException;

    Optional<Boolean> asBoolean();
    Optional<Character> asCharacter();
    Optional<Integer> asInteger();
    Optional<Long> asLong();
    Optional<Float> asFloat();
    Optional<Double> asDouble();

}
