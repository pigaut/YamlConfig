package io.github.pigaut.yaml.convert.format;

import org.jetbrains.annotations.*;

@FunctionalInterface
public interface StringFormatter extends Formatter<String> {

    @Override
    @NotNull String format(@NotNull String value);

}
