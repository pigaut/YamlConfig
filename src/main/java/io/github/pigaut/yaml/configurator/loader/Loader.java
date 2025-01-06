package io.github.pigaut.yaml.configurator.loader;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;

import java.util.function.*;

public class Loader {

    public static <T> FieldLoader<T> loadField(Function<ConfigField, T> loader) {
        return new FieldLoader<T>() {
            @Override
            public @NotNull T loadFromField(ConfigField field) throws InvalidConfigurationException {
                return loader.apply(field);
            }
        };
    }

    public static <T> FieldLoader<T> loadField(String key, Function<ConfigField, T> loader) {
        return new FieldLoader<T>() {
            @Override
            public @Nullable String getKey() {
                return key;
            }
            @Override
            public @NotNull T loadFromField(ConfigField field) throws InvalidConfigurationException {
                return loader.apply(field);
            }
        };
    }

    public static <T> SectionLoader<T> loadSection(Function<ConfigSection, T> loader) {
        return new SectionLoader<T>() {
            @Override
            public @NotNull T loadFromSection(ConfigSection section) throws InvalidConfigurationException {
                return loader.apply(section);
            }
        };
    }

    public static <T> SectionLoader<T> loadSection(String key, Function<ConfigSection, T> loader) {
        return new SectionLoader<T>() {
            @Override
            public @Nullable String getKey() {
                return key;
            }
            @Override
            public @NotNull T loadFromSection(ConfigSection section) throws InvalidConfigurationException {
                return loader.apply(section);
            }
        };
    }

    public static <T> SequenceLoader<T> loadSequence(Function<ConfigSequence, T> loader) {
        return new SequenceLoader<T>() {
            @Override
            public @NotNull T loadFromSequence(ConfigSequence sequence) throws InvalidConfigurationException {
                return loader.apply(sequence);
            }
        };
    }

    public static <T> SequenceLoader<T> loadSequence(String key, Function<ConfigSequence, T> loader) {
        return new SequenceLoader<T>() {
            @Override
            public @Nullable String getKey() {
                return key;
            }

            @Override
            public @NotNull T loadFromSequence(ConfigSequence sequence) throws InvalidConfigurationException {
                return loader.apply(sequence);
            }
        };
    }

    public static <T> ScalarLoader<T> loadScalar(Function<ConfigScalar, T> loader) {
        return new ScalarLoader<T>() {
            @Override
            public @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
                return loader.apply(scalar);
            }
        };
    }

    public static <T> ScalarLoader<T> loadScalar(String key, Function<ConfigScalar, T> loader) {
        return new ScalarLoader<T>() {
            @Override
            public @Nullable String getKey() {
                return key;
            }
            @Override
            public @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
                return loader.apply(scalar);
            }
        };
    }
}
