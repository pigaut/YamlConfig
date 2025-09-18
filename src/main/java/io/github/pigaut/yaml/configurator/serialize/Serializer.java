package io.github.pigaut.yaml.configurator.serialize;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.map.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Serializer<T> extends ConfigMapper<T> {

    @NotNull
    String serialize(@NotNull T value);

    @Override
    default @NotNull FieldType getDefaultMappingType() {
        return FieldType.SCALAR;
    }

    @Override
    default void mapToScalar(@NotNull ConfigScalar scalar, @NotNull T value) {
        final String serializedValue = serialize(value);
        scalar.setValue(serializedValue);
    }

}
