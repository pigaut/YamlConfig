package io.github.pigaut.yamlib.parser.deserializer;

import io.github.pigaut.yamlib.parser.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Deserializer<R> {

    @NotNull
    R deserialize(@NotNull String string) throws DeserializationException;

}
