package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface FieldLoader<T> extends ConfigLoader<T> {

    @NotNull T loadFromField(ConfigField field) throws InvalidConfigurationException;

    @Override
    default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
        return loadFromField(scalar);
    }

    @Override
    default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
        return loadFromField(section);
    }

    @Override
    default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
        return loadFromField(sequence);
    }

}
