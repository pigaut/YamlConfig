package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import org.jetbrains.annotations.*;

public interface ConfigLoader<T> {

    default @Nullable String getProblemDescription() {
        return null;
    }

    default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(scalar, "Value is not supported here");
    }

    default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(section, "Section is not supported here");
    }

    default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(sequence, "Sequence (list) is not supported here");
    }

    @FunctionalInterface
    interface Scalar<T> extends ConfigLoader<T> {

        @NotNull
        T loadFromScalar(ConfigScalar line) throws InvalidConfigurationException;

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(section, "Section not supported for scalar loader");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(sequence, "Sequence not supported for scalar loader");
        }
    }

    @FunctionalInterface
    interface Line<T> extends ConfigLoader<T> {

        @NotNull
        T loadFromLine(ConfigLine line) throws InvalidConfigurationException;

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
            return loadFromLine(scalar.toLine());
        }

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(section, "loadFromSection is not supported for line loader");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(sequence, "loadFromSequence is not supported for line loader");
        }
    }

    @FunctionalInterface
    interface Section<T> extends ConfigLoader<T> {

        @Override
        @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException;

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(scalar, "Scalar not supported for section loader");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(sequence, "Sequence not supported for section loader");
        }
    }

    @FunctionalInterface
    interface Sequence<T> extends ConfigLoader<T> {

        @Override
        @NotNull T loadFromSequence(@NotNull ConfigSequence section) throws InvalidConfigurationException;

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(scalar, "Scalar not supported for sequence loader");
        }

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
            throw new InvalidConfigurationException(section, "Section not supported for sequence loader");
        }
    }

    @FunctionalInterface
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

    @FunctionalInterface
    interface Any<T> extends ConfigLoader<T> {
        @NotNull T loadFromField(@NotNull ConfigField field) throws InvalidConfigurationException;

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

}
