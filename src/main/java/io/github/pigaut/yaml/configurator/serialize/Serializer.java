package io.github.pigaut.yaml.configurator.serialize;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.convert.format.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Serializer<T> extends ConfigMapper<T> {

    @NotNull String serialize(@NotNull T value);

    @Override
    default @NotNull FieldType getDefaultMappingType() {
        return FieldType.SCALAR;
    }

    @Override
    default @NotNull Object createScalar(@NotNull T value) {
        return serialize(value);
    }

}
