package io.github.pigaut.yaml.convert.format;

import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Formatter<T> {

    @NotNull String format(@NotNull T value);

}
