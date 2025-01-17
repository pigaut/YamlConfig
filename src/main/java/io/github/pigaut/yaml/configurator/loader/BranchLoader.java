package io.github.pigaut.yaml.configurator.loader;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

public interface BranchLoader<T> extends ConfigLoader<T> {

    @NotNull T loadFromBranch(ConfigBranch branch) throws InvalidConfigurationException;

    @Override
    default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
        return loadFromBranch((ConfigBranch) section);
    }

    @Override
    default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
        return loadFromBranch((ConfigBranch) sequence);
    }

}
