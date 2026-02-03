package io.github.pigaut.yaml;

import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ConfigOptional<T> extends AbstractOptional<T> {

    protected ConfigOptional(@NotNull ConfigField field, @NotNull T value, boolean existsInConfig) {
        super(field, value, existsInConfig);
    }

    protected ConfigOptional(@NotNull ConfigField field, @NotNull InvalidConfigurationException exception, boolean existsInConfig) {
        super(field, exception, existsInConfig);
    }

    public static <T extends ConfigField> ConfigOptional<T> of(@NotNull T field) {
        return new ConfigOptional<>(field, field, true);
    }

    public static <T> ConfigOptional<T> of(@NotNull ConfigField field, @NotNull T value) {
        return new ConfigOptional<>(field, value, true);
    }

    public static <T> ConfigOptional<T> notSet(@NotNull ConfigField field, @NotNull String cause) {
        return new ConfigOptional<>(field, new InvalidConfigurationException(field, cause), false);
    }

    public static <T> ConfigOptional<T> notSet(@NotNull ConfigField field, @NotNull String key, @NotNull String cause) {
        return new ConfigOptional<>(field, new InvalidConfigurationException(field, key, cause), false);
    }

    public static <T> ConfigOptional<T> notSet(@NotNull ConfigField field, int index, @NotNull String cause) {
        return new ConfigOptional<>(field, new InvalidConfigurationException(field, index, cause), false);
    }

    public static <T> ConfigOptional<T> invalid(@NotNull ConfigField field, @NotNull String cause) {
        return new ConfigOptional<>(field, new InvalidConfigurationException(field, cause), true);
    }

    public static <T> ConfigOptional<T> invalid(@NotNull InvalidConfigurationException exception) {
        return new ConfigOptional<>(exception.getField(), exception, true);
    }

    public Stream<T> stream() {
        if (!isValid()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    public ConfigOptional<T> or(@NotNull T other) {
        if (isValid()) {
            return this;
        } else {
            return new ConfigOptional<>(field, other, existsInConfig);
        }
    }

    public ConfigOptional<T> require(@NotNull Requirement<? super T> requirement) {
        return require(requirement, requirement.getErrorDetails());
    }

    public ConfigOptional<T> require(@NotNull Requirement<? super T> requirement, @NotNull String errorDetails) {
        Objects.requireNonNull(requirement);
        Objects.requireNonNull(errorDetails);
        if (isInvalid()) {
            return this;
        } else {
            if (requirement.test(value)) {
                return this;
            }
            return new ConfigOptional<>(field, new InvalidConfigurationException(field, errorDetails), existsInConfig);
        }
    }

    public ConfigOptional<T> check(boolean condition, @NotNull String errorMessage) {
        if (isInvalid()) {
            return this;
        } else {
            if (condition) {
                return this;
            }
            return new ConfigOptional<>(field, new InvalidConfigurationException(field, errorMessage), existsInConfig);
        }
    }

    public <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (exception != null) {
            return new ConfigOptional<>(field, exception, existsInConfig);
        } else {
            U mappedValue = Objects.requireNonNull(mapper.apply(value));
            return new ConfigOptional<>(field, mappedValue, existsInConfig);
        }
    }

    public <U> ConfigOptional<U> flatMap(Function<? super T, ? extends ConfigOptional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (exception != null) {
            return new ConfigOptional<>(field, exception, existsInConfig);
        } else {
            @SuppressWarnings("unchecked")
            ConfigOptional<U> r = (ConfigOptional<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    public <U> ConfigList<U> mapToList(Function<? super T, ? extends ConfigList<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (exception != null) {
            return new ConfigList<>(field, exception, existsInConfig);
        } else {
            @SuppressWarnings("unchecked")
            ConfigList<U> r = (ConfigList<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

}
