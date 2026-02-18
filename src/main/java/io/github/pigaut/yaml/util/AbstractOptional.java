package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class AbstractOptional<T> {

    protected final ConfigField field;
    protected final T value;
    protected final InvalidConfigException exception;
    protected final boolean existsInConfig;

    protected AbstractOptional(@NotNull ConfigField field, @NotNull T value, boolean existsInConfig) {
        this.field = field;
        this.value = value;
        this.exception = null;
        this.existsInConfig = existsInConfig;
    }

    protected AbstractOptional(@NotNull ConfigField field, @NotNull InvalidConfigException exception, boolean existsInConfig) {
        this.field = field;
        this.value = null;
        this.exception = exception;
        this.existsInConfig = existsInConfig;
    }

    public ConfigField field() {
        return field;
    }

    public @NotNull T value() throws NoSuchElementException {
        if (value == null) {
            throw new NoSuchElementException("No value present in config optional.");
        }
        return value;
    }

    public @NotNull InvalidConfigException error() throws NoSuchElementException {
        if (exception == null) {
            throw new NoSuchElementException("No exception is present in config optional.");
        }
        return exception;
    }

    public boolean existsInConfig() {
        return existsInConfig;
    }

    public boolean isValid() {
        return value != null;
    }

    public boolean isInvalid() {
        return exception != null;
    }

    public void ifValid(Consumer<? super T> action) throws InvalidConfigException {
        if (isValid()) {
            action.accept(value);
        }
        else if (existsInConfig()) {
            throw exception;
        }
    }

    public void ifValidOrElse(Consumer<? super T> action, Consumer<InvalidConfigException> errorCollector) {
        if (isValid()) {
            action.accept(value);
        }
        else if (existsInConfig()) {
            errorCollector.accept(exception);
        }
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    public void collectError(Consumer<InvalidConfigException> errorCollector) throws IllegalStateException {
        if (isValid()) {
            throw new IllegalStateException("There is no error in this config optional.");
        }
        errorCollector.accept(exception);
    }

    public @NotNull T orThrow() throws InvalidConfigException {
        if (exception != null) {
            throw exception;
        }
        return value;
    }

    public T withDefault(T defaultValue) throws InvalidConfigException {
        if (isValid()) {
            return value;
        }
        else if (existsInConfig()) {
            throw exception;
        }
        return defaultValue;
    }

    public T withDefaultOrElse(T defaultValue, Consumer<InvalidConfigException> errorCollector) {
        if (isValid()) {
            return value;
        }
        else if (existsInConfig()) {
            errorCollector.accept(exception);
        }
        return defaultValue;
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AbstractOptional<?> that)) return false;
        return existsInConfig == that.existsInConfig && Objects.equals(field, that.field) && Objects.equals(value, that.value) && Objects.equals(exception, that.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value, exception, existsInConfig);
    }

}
