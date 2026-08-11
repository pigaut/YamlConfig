package io.github.pigaut.yaml;

import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ConfigOptional<T> extends AbstractOptional<T> {

    protected ConfigOptional(ConfigField field, @Nullable T value, boolean existsInConfig) {
        super(field, value, existsInConfig);
    }

    protected ConfigOptional(ConfigField field, @Nullable InvalidConfigException exception, boolean existsInConfig) {
        super(field, exception, existsInConfig, false);
    }

    protected ConfigOptional(ConfigField field, @Nullable InvalidConfigException exception, boolean existsInConfig, boolean warning) {
        super(field, exception, existsInConfig, warning);
    }

    public static <T extends ConfigField> ConfigOptional<T> of(@NotNull T field) {
        return new ConfigOptional<>(field, field, true);
    }

    public static <T> ConfigOptional<T> of(@NotNull ConfigField field, @NotNull T value) {
        return new ConfigOptional<>(field, value, true);
    }

    public static <T> ConfigOptional<T> notSet(@NotNull ConfigField field, @NotNull String cause) {
        return new ConfigOptional<>(field, new InvalidConfigException(field, cause), false);
    }

    public static <T> ConfigOptional<T> notSet(@NotNull ConfigField field, @NotNull String key, @NotNull String cause) {
        return new ConfigOptional<>(field, new InvalidConfigException(field, key, cause), false);
    }

    public static <T> ConfigOptional<T> notSet(@NotNull ConfigField field, int index, @NotNull String cause) {
        return new ConfigOptional<>(field, new InvalidConfigException(field, index, cause), false);
    }

    public static <T> ConfigOptional<T> invalid(@NotNull ConfigField field, @NotNull String details) {
        return new ConfigOptional<>(field, new InvalidConfigException(field, details), true);
    }

    public static <T> ConfigOptional<T> invalid(@NotNull InvalidConfigException exception) {
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
            return new ConfigOptional<>(field, new InvalidConfigException(field, errorDetails), existsInConfig);
        }
    }

    public ConfigOptional<T> requireOrWarn(@NotNull Requirement<? super T> requirement) {
        return requireOrWarn(requirement, requirement.getErrorDetails());
    }

    public ConfigOptional<T> requireOrWarn(@NotNull Requirement<? super T> requirement, @NotNull String errorDetails) {
        Objects.requireNonNull(requirement);
        Objects.requireNonNull(errorDetails);
        if (isInvalid()) {
            return this;
        } else {
            if (requirement.test(value)) {
                return this;
            }
            return new ConfigOptional<>(field, new InvalidConfigException(field, errorDetails), existsInConfig, true);
        }
    }

    public T requireOrThrow(@NotNull Requirement<? super T> requirement) throws InvalidConfigException {
        return requireOrThrow(requirement, requirement.getErrorDetails());
    }

    public T requireOrThrow(@NotNull Requirement<? super T> requirement, @NotNull String errorDetails) throws InvalidConfigException {
        Objects.requireNonNull(requirement);
        Objects.requireNonNull(errorDetails);
        if (isInvalid()) {
            throw exception;
        } else {
            if (requirement.test(value)) {
                return value;
            }
            throw new InvalidConfigException(field, errorDetails);
        }
    }

    public ConfigOptional<T> check(boolean condition, @NotNull String errorMessage) {
        if (isInvalid()) {
            return this;
        } else {
            if (condition) {
                return this;
            }
            return new ConfigOptional<>(field, new InvalidConfigException(field, errorMessage), existsInConfig);
        }
    }

    public ConfigOptional<T> checkOrWarn(boolean condition, @NotNull String errorMessage) {
        if (isInvalid()) {
            return this;
        } else {
            if (condition) {
                return this;
            }
            return new ConfigOptional<>(field, new InvalidConfigException(field, errorMessage), existsInConfig, true);
        }
    }

    public <U> ConfigOptional<@Nullable U> mapIfValid(Function<? super T, ? extends @Nullable U> mapper) {
        Objects.requireNonNull(mapper);
        if (isInvalid()) {
            return new ConfigOptional<>(field, exception, existsInConfig);
        }
        U mappedValue = Objects.requireNonNull(mapper.apply(value));
        return new ConfigOptional<>(field, mappedValue, existsInConfig);
    }

    public <U> ConfigOptional<@Nullable U> flatMapIfValid(Function<? super T, ? extends ConfigOptional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isInvalid()) {
            return new ConfigOptional<>(field, exception, existsInConfig);
        }
        @SuppressWarnings("unchecked")
        ConfigOptional<U> r = (ConfigOptional<U>) mapper.apply(value);
        return Objects.requireNonNull(r);
    }

    public <U> ConfigList<@Nullable U> mapToListIfValid(Function<? super T, ? extends ConfigList<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isInvalid()) {
            return new ConfigList<>(field, exception, existsInConfig);
        }
        @SuppressWarnings("unchecked")
        ConfigList<U> r = (ConfigList<U>) mapper.apply(value);
        return Objects.requireNonNull(r);
    }

}
