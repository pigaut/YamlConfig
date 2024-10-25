package io.github.pigaut.yamlib.config.configurator;

import io.github.pigaut.yamlib.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ConfigLoader<T> {

    @NotNull
    T load(@NotNull ConfigSection section) throws InvalidConfigurationException;

}
