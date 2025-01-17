package io.github.pigaut.yaml.configurator.parser;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.mapper.*;
import io.github.pigaut.yaml.parser.serializer.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ConfigSerializer<T> extends ConfigMapper<T>, Serializer<T> {

    @Override
    default @NotNull FieldType getDefaultMappingType() {
        return FieldType.SCALAR;
    }

    @Override
    default @NotNull Object createScalar(@NotNull T value) {
        return serialize(value);
    }

}
