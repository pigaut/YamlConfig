package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.regex.*;

public interface ConfigScalar extends ConfigField {

    boolean contains(String value);

    @NotNull Object getValue();
    @NotNull ScalarStyle getScalarStyle();

    void setValue(@Nullable Object value);
    void setScalarStyle(@NotNull ScalarStyle scalarStyle);

    @NotNull String toString();
    @NotNull String toString(@NotNull StringFormatter formatter);

    ConfigLine toLine();
    ConfigSequence split(Pattern pattern);

    ConfigOptional<Boolean> toBoolean();
    ConfigOptional<Character> toCharacter();
    ConfigOptional<Integer> toInteger();
    ConfigOptional<Long> toLong();
    ConfigOptional<Float> toFloat();
    ConfigOptional<Double> toDouble();

}
