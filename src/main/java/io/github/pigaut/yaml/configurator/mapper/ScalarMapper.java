package io.github.pigaut.yaml.configurator.mapper;

import io.github.pigaut.yaml.configurator.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ScalarMapper<T> extends ConfigMapper<T> {

    @NotNull Object createScalar(@NotNull T value);

    @Override
    default @NotNull MappingType getDefaultMappingType() {
        return MappingType.SCALAR;
    }

}
