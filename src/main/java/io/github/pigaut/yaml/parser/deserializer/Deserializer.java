package io.github.pigaut.yaml.parser.deserializer;

import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Deserializer<R> {

    @NotNull
    R deserialize(@NotNull String string) throws DeserializationException;

}
