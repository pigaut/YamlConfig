package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.*;
import io.github.pigaut.yaml.node.scalar.*;
import org.jetbrains.annotations.*;

public interface ConfigLoader<T> {

    default @Nullable String getErrorDescription() {
        return null;
    }

    default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
        throw new InvalidConfigException(scalar, "Value is not supported here");
    }

    default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
        throw new InvalidConfigException(section, "Section is not supported here");
    }

    default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
        throw new InvalidConfigException(sequence, "List is not supported here");
    }

    @FunctionalInterface
    interface Scalar<T> extends ConfigLoader<T> {

        @NotNull
        T loadFromScalar(ConfigScalar line) throws InvalidConfigException;

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
            throw new InvalidConfigException(section, "Section not supported for scalar loader");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
            throw new InvalidConfigException(sequence, "Sequence not supported for scalar loader");
        }
    }

    @FunctionalInterface
    interface Line<T> extends ConfigLoader<T> {

        @NotNull
        T loadFromLine(ConfigLine line) throws InvalidConfigException;

        default @NotNull LineStyle getLineStyle() {
            return LineStyle.LABELED;
        }

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
            LineStyle lineStyle = getLineStyle();
            return loadFromLine(scalar.toLine(lineStyle));
        }

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
            throw new InvalidConfigException(section, "Expected a line but found a section");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
            throw new InvalidConfigException(sequence, "Expected a line but found a list");
        }
    }

    @FunctionalInterface
    interface Section<T> extends ConfigLoader<T> {

        @Override
        @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException;

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
            throw new InvalidConfigException(scalar, "Expected a section but found a value");
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
            throw new InvalidConfigException(sequence, "Expected a section but found a list");
        }
    }

    @FunctionalInterface
    interface Sequence<T> extends ConfigLoader<T> {

        @Override
        @NotNull T loadFromSequence(@NotNull ConfigSequence section) throws InvalidConfigException;

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
            throw new InvalidConfigException(scalar, "Expected a list but found a value");
        }

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
            throw new InvalidConfigException(section, "Expected a list but found a section");
        }
    }

    @FunctionalInterface
    interface Branch<T> extends ConfigLoader<T> {
        @NotNull T loadFromBranch(@NotNull ConfigBranch branch) throws InvalidConfigException;

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
            return loadFromBranch(section);
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
            return loadFromBranch(sequence);
        }
    }

    @FunctionalInterface
    interface Any<T> extends ConfigLoader<T> {
        @NotNull T loadFromField(@NotNull ConfigField field) throws InvalidConfigException;

        @Override
        default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
            return loadFromField(scalar);
        }

        @Override
        default @NotNull T loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
            return loadFromField(section);
        }

        @Override
        default @NotNull T loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
            return loadFromField(sequence);
        }

    }

}
