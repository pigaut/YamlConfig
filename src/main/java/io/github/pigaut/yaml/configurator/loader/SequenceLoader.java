package io.github.pigaut.yaml.configurator.loader;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface SequenceLoader<T>  extends ConfigLoader<T> {

    @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException;

}
