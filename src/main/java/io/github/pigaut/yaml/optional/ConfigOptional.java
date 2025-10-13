package io.github.pigaut.yaml.optional;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ConfigOptional<T> {

    private final ConfigField field;
    private final T value;
    private final InvalidConfigurationException exception;
    private final boolean existsInConfig;

    private ConfigOptional(@NotNull ConfigField field, @NotNull T value, boolean existsInConfig) {
        this.field = field;
        this.value = value;
        this.exception = null;
        this.existsInConfig = existsInConfig;
    }

    private ConfigOptional(@NotNull ConfigField field, @NotNull InvalidConfigurationException exception, boolean existsInConfig) {
        this.field = field;
        this.value = null;
        this.exception = exception;
        this.existsInConfig = existsInConfig;
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

    public @NotNull T value() throws NoSuchElementException {
        if (value == null) {
            throw new NoSuchElementException("No value present in config optional.");
        }
        return value;
    }

    public @NotNull InvalidConfigurationException error() throws NoSuchElementException {
        if (exception == null) {
            throw new NoSuchElementException("No exception is present in config optional.");
        }
        return exception;
    }

    public boolean isSetInConfig() {
        return existsInConfig;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return exception != null;
    }

    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    public void ifPresentOrElse(Consumer<? super T> action, Consumer<InvalidConfigurationException> emptyAction) {
        if (isPresent()) {
            action.accept(value);
        } else {
            emptyAction.accept(exception);
        }
    }

    public ConfigOptional<T> filter(Predicate<? super T> predicate, String errorMessage) {
        Objects.requireNonNull(predicate);
        if (isEmpty()) {
            return this;
        } else {
            if (predicate.test(value)) {
                return this;
            }
            return new ConfigOptional<>(field, new InvalidConfigurationException(field, errorMessage), existsInConfig);
        }
    }

    public ConfigOptional<T> filter(boolean condition, String errorMessage) {
        if (isEmpty()) {
            return this;
        }
        else {
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
        }
        else {
            return new ConfigOptional<>(field, mapper.apply(value), existsInConfig);
        }
    }

    public <U> ConfigOptional<U> flatMap(Function<? super T, ? extends ConfigOptional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (exception != null) {
            return new ConfigOptional<>(field, exception, existsInConfig);
        }
        else {
            @SuppressWarnings("unchecked")
            ConfigOptional<U> r = (ConfigOptional<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    public ConfigOptional<T> or(@NotNull T other) {
        if (isPresent()) {
            return this;
        } else {
            return new ConfigOptional<>(field, other, existsInConfig);
        }
    }

    public Stream<T> stream() {
        if (!isPresent()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public void collectError(Consumer<InvalidConfigurationException> errorCollector) throws IllegalStateException {
        if (isPresent()) {
            throw new IllegalStateException("There is no error in this config optional.");
        }
        errorCollector.accept(exception);
    }

    public @NotNull T orThrow() throws InvalidConfigurationException {
        if (exception != null) {
            throw exception;
        }
        return value;
    }

    public T withDefault(T defaultValue) throws InvalidConfigurationException {
        if (isPresent()) {
            return value;
        }
        if (isSetInConfig()) {
            throw exception;
        }
        return defaultValue;
    }

    public T withDefault(T defaultValue, Consumer<InvalidConfigurationException> errorCollector) {
        if (isPresent()) {
            return value;
        }
        if (isSetInConfig()) {
            errorCollector.accept(exception);
        }
        return defaultValue;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ConfigOptional<?> that)) return false;
        return existsInConfig == that.existsInConfig && Objects.equals(field, that.field) && Objects.equals(value, that.value) && Objects.equals(exception, that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value, exception, existsInConfig);
    }

}
