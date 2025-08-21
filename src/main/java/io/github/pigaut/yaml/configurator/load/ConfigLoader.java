package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

public interface ConfigLoader<T> {

    default @Nullable String getProblemDescription() {
        return null;
    }

    default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(scalar, "Values are not supported here");
    }

    default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(section, "Sections are not supported here");
    }

    default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(sequence, "Lists are not supported here");
    }

}
