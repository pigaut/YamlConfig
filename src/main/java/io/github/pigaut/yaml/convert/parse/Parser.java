package io.github.pigaut.yaml.convert.parse;

import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Parser<R> {

    @NotNull
    R parse(@NotNull String string) throws StringParseException;

}
