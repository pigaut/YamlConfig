package io.github.pigaut.yamlib.parser.serializer;

import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Serializer<T> {

    @NotNull String serialize(@NotNull T value);

}
