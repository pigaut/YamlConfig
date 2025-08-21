package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface SectionLoader<T> extends ConfigLoader<T> {

    @NotNull
    T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException;

}
