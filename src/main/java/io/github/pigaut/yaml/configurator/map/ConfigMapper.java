package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.FieldType;
import org.jetbrains.annotations.*;

public interface ConfigMapper<T> {

    @NotNull
    FieldType getDefaultMappingType();

    default boolean clearExistingFields() {
        return true;
    }

    default @NotNull String createKey(@NotNull T value) {
        return YamlConfig.generateRandomKey();
    }

    default void mapToScalar(@NotNull ConfigScalar scalar, @NotNull T value) {
        throw new UnsupportedMappingException(getClass().getSimpleName() + " does not support scalars");
    }

    default void mapToSection(@NotNull ConfigSection section, @NotNull T value) {
        throw new UnsupportedMappingException(getClass().getSimpleName() + " does not support sections");
    }

    default void mapToSequence(@NotNull ConfigSequence sequence, @NotNull T value) {
        throw new UnsupportedMappingException(getClass().getSimpleName() + " does not support lists");
    }

    @FunctionalInterface
    interface Scalar<T> extends ConfigMapper<T> {

        void mapToScalar(@NotNull ConfigScalar scalar, @NotNull T value);

        @Override
        default @NotNull FieldType getDefaultMappingType() {
            return FieldType.SCALAR;
        }

    }

    @FunctionalInterface
    interface Line<T> extends Scalar<T> {

        void mapToLine(@NotNull ConfigLine line, @NotNull T value);

        @Override
        default void mapToScalar(@NotNull ConfigScalar scalar, @NotNull T value) {
            mapToLine(scalar.toLine(), value);
        }

    }

    @FunctionalInterface
    interface Section<T> extends ConfigMapper<T> {

        void mapToSection(@NotNull ConfigSection section, @NotNull T value);

        @Override
        default @NotNull FieldType getDefaultMappingType() {
            return FieldType.SECTION;
        }

    }

    @FunctionalInterface
    interface Sequence<T> extends ConfigMapper<T> {

        void mapToSequence(@NotNull ConfigSequence sequence, @NotNull T value);

        @Override
        default @NotNull FieldType getDefaultMappingType() {
            return FieldType.SEQUENCE;
        }

    }

}
