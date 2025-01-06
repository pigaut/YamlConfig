package io.github.pigaut.yaml.configurator.loader;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ScalarLoader<T> extends ConfigLoader<T> {

    @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException;

}
