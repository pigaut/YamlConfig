package io.github.pigaut.yamlib.configurator.section;

import io.github.pigaut.yamlib.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ConfigLoader<T> {

    default boolean matchSchema(ConfigSection section) {
        return true;
    }

    @NotNull
    T load(@NotNull ConfigSection section) throws InvalidConfigurationException;

}
