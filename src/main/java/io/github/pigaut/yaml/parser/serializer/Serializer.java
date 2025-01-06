package io.github.pigaut.yaml.parser.serializer;

import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Serializer<T> {

    @NotNull String serialize(@NotNull T value);

}
