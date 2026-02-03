package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

public interface KeyedField extends ConfigField {

    @NotNull String getKey();
    @NotNull ConfigScalar getKeyAsScalar();

    <T> ConfigOptional<T> getKeyAs(Class<T> classType);
    ConfigOptional<Boolean> getBooleanKey();
    ConfigOptional<Character> getCharacterKey();
    ConfigOptional<Integer> getIntegerKey();
    ConfigOptional<Long> getLongKey();
    ConfigOptional<Float> getFloatKey();
    ConfigOptional<Double> getDoubleKey();

}
