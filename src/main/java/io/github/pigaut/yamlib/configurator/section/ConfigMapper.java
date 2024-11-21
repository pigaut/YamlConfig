package io.github.pigaut.yamlib.configurator.section;

import io.github.pigaut.yamlib.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ConfigMapper<T> {

    default @Nullable String generateKey(T value) {
        return null;
    }

    void map(ConfigSection section, T value);

}
