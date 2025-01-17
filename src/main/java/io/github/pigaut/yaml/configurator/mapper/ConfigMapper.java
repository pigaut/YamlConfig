package io.github.pigaut.yaml.configurator.mapper;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.FieldType;
import org.jetbrains.annotations.*;

public interface ConfigMapper<T> {

    @NotNull
    FieldType getDefaultMappingType();

    default boolean keepExistingFields() {
        return false;
    }

    default @NotNull String createKey(@NotNull T value) {
        return YamlConfig.generateRandomKey();
    }

    default @NotNull Object createScalar(@NotNull T value) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support scalars");
    }

    default void mapSection(@NotNull ConfigSection section, @NotNull T value) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support sections");
    }

    default void mapSequence(@NotNull ConfigSequence sequence, @NotNull T value) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support lists");
    }

}
