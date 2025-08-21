package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.scalar.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public interface ConfigScalar extends ConfigField {

    static RootScalar loadConfiguration(File file) {
        final RootScalar config = new RootScalar(file);
        config.load();
        return config;
    }

    static RootScalar loadConfiguration(File file, Configurator configurator) {
        final RootScalar config = new RootScalar(file, configurator);
        config.load();
        return config;
    }

    @NotNull
    Object getValue();

    void setValue(@NotNull Object value);

    @NotNull
    ScalarStyle getScalarStyle();

    void setScalarStyle(@NotNull ScalarStyle scalarStyle);

    boolean toBoolean() throws InvalidConfigurationException;

    char toCharacter() throws InvalidConfigurationException;

    @NotNull
    String toString();

    @NotNull
    String toString(@NotNull StringFormatter formatter);

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

    ConfigSequence split(String regex);

    ConfigSequence split(Pattern pattern);

}
