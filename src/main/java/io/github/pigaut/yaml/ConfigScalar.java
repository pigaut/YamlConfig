package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

public interface ConfigScalar extends ConfigField {

    @NotNull
    Object getValue();

    void setValue(@NotNull Object value);

    @NotNull
    ScalarStyle getScalarStyle();

    void setScalarStyle(@NotNull ScalarStyle scalarStyle);

    @NotNull String toString();

    @NotNull String toString(@NotNull StringFormatter formatter);

    ConfigOptional<Boolean> toBoolean();

    ConfigOptional<Character> toCharacter();

    ConfigOptional<Integer> toInteger();

    ConfigOptional<Long> toLong();

    ConfigOptional<Float> toFloat();

    ConfigOptional<Double> toDouble();

    ConfigLine toLine();

}
