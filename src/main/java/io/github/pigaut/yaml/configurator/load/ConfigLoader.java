package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

public interface ConfigLoader<T> {

    default @Nullable String getProblemDescription() {
        return null;
    }

    default @NotNull T loadFromLine(ConfigLine line) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(line, "In-line data is not supported here");
    }

    default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(scalar, "Scalar value is not supported here");
    }

    default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(section, "Section is not supported here");
    }

    default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(sequence, "Sequence (list) is not supported here");
    }

    interface Scalar<T> extends ConfigLoader<T> {
        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(section, "Section not supported for scalar loader");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(sequence, "Sequence not supported for scalar loader");
        }
    }

    interface Section<T> extends ConfigLoader<T> {
        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(scalar, "Scalar not supported for section loader");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(sequence, "Sequence not supported for section loader");
        }
    }

    interface Sequence<T> extends ConfigLoader<T> {
        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(scalar, "Scalar not supported for sequence loader");
        }

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(section, "Section not supported for sequence loader");
        }
    }

    interface Branch<T> extends ConfigLoader<T> {
        @NotNull T loadFromBranch(@NotNull ConfigBranch branch) throws InvalidConfigurationException;

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
            return loadFromBranch(section);
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
            return loadFromBranch(sequence);
        }
    }

    interface Field<T> extends ConfigLoader<T> {
        @NotNull T loadFromField(@NotNull ConfigField field) throws InvalidConfigurationException;

        @Override
        default @NotNull T loadFromLine(ConfigLine line) throws InvalidConfigurationException {
            return loadFromField(line);
        }

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
            return loadFromField(scalar);
        }
    }

}
