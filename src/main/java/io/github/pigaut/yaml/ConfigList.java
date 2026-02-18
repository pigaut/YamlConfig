package io.github.pigaut.yaml;

import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ConfigList<E> extends AbstractOptional<List<E>> {

    protected ConfigList(@NotNull ConfigField field, @NotNull List<E> elements, boolean existsInConfig) {
        super(field, elements, existsInConfig);
    }

    protected ConfigList(@NotNull ConfigField field, @NotNull InvalidConfigException exception, boolean existsInConfig) {
        super(field, exception, existsInConfig);
    }

    public static <E> ConfigList<E> of(@NotNull ConfigField field, @NotNull List<E> elements) {
        return new ConfigList<>(field, elements, true);
    }

    public static <E> ConfigList<E> invalid(@NotNull InvalidConfigException exception) {
        return new ConfigList<>(exception.getField(), exception, true);
    }

    public List<E> orEmpty() throws InvalidConfigException {
        if (isValid()) {
            return value;
        }
        else if (existsInConfig()) {
            throw exception;
        }
        return List.of();
    }

    public Stream<E> stream() {
        if (!isValid()) {
            return Stream.empty();
        }
        else {
            return value.stream();
        }
    }

    public ConfigList<E> or(@NotNull List<E> other) {
        if (isValid()) {
            return this;
        } else {
            return new ConfigList<>(field, other, existsInConfig);
        }
    }

    public ConfigList<E> require(@NotNull Requirement<? super List<E>> requirement) {
        return require(requirement, requirement.getErrorDetails());
    }

    public ConfigList<E> require(@NotNull Requirement<? super List<E>> requirement, @NotNull String errorDetails) {
        Objects.requireNonNull(requirement);
        Objects.requireNonNull(errorDetails);
        if (isInvalid()) {
            return this;
        }
        else {
            if (requirement.test(value)) {
                return this;
            }
            return new ConfigList<>(field, new InvalidConfigException(field, errorDetails), existsInConfig);
        }
    }

    public List<E> requireOrThrow(@NotNull Requirement<? super List<E>> requirement) throws InvalidConfigException {
        return requireOrThrow(requirement, requirement.getErrorDetails());
    }

    public List<E> requireOrThrow(@NotNull Requirement<? super List<E>> requirement, @NotNull String errorDetails) throws InvalidConfigException {
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

    public ConfigList<E> requireEach(@NotNull Requirement<? super E> requirement) {
        return requireEach(requirement, requirement.getErrorDetails());
    }

    public ConfigList<E> requireEach(@NotNull Requirement<? super E> requirement, @NotNull String errorDetails) {
        Objects.requireNonNull(requirement);
        Objects.requireNonNull(errorDetails);
        if (isInvalid()) {
            return this;
        }
        else {
            for (E element : value) {
                if (!requirement.test(element)) {
                    return new ConfigList<>(field, new InvalidConfigException(field, errorDetails), existsInConfig);
                }
            }
            return this;
        }
    }

    public void forEach(@NotNull Consumer<E> action) throws InvalidConfigException {
        Objects.requireNonNull(action);
        if (isValid()) {
            for (E element : value) {
                action.accept(element);
            }
        }
        else if (existsInConfig()) {
            throw exception;
        }
    }

    public void forEachOrElse(@NotNull Consumer<E> action, @NotNull Consumer<InvalidConfigException> errorCollector) {
        Objects.requireNonNull(action);
        Objects.requireNonNull(errorCollector);
        if (isValid()) {
            for (E element : value) {
                action.accept(element);
            }
        }
        else if (existsInConfig()) {
            errorCollector.accept(exception);
        }
    }

    public ConfigList<E> check(boolean condition, @NotNull String errorDetails) {
        Objects.requireNonNull(errorDetails);
        if (isInvalid()) {
            return this;
        }
        else {
            if (condition) {
                return this;
            }
            return new ConfigList<>(field, new InvalidConfigException(field, errorDetails), existsInConfig);
        }
    }

    public <U> ConfigOptional<U> map(Function<? super List<E>, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (exception != null) {
            return new ConfigOptional<>(field, exception, existsInConfig);
        }
        else {
            U mappedValue = Objects.requireNonNull(mapper.apply(value));
            return new ConfigOptional<>(field, mappedValue, existsInConfig);
        }
    }

    public <U> ConfigList<U> flatMap(Function<? super List<E>, ? extends ConfigList<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (exception != null) {
            return new ConfigList<>(field, exception, existsInConfig);
        }
        else {
            @SuppressWarnings("unchecked")
            ConfigList<U> r = (ConfigList<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

}
