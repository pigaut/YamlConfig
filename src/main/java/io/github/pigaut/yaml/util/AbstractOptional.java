package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.load.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class AbstractOptional<T> {

    protected final ConfigField field;
    protected final T value;
    protected final InvalidConfigException exception;
    protected final boolean existsInConfig;
    protected final boolean warning;

    protected AbstractOptional(ConfigField field, @Nullable T value, boolean existsInConfig) {
        this.field = field;
        this.value = value;
        this.exception = null;
        this.existsInConfig = existsInConfig;
        this.warning = false;
    }

    protected AbstractOptional(ConfigField field, @Nullable InvalidConfigException exception, boolean existsInConfig, boolean warning) {
        this.field = field;
        this.value = null;
        this.exception = exception;
        this.existsInConfig = existsInConfig;
        this.warning = warning;
        ConfigRoot root = field.getRoot();
        ConfigLoader<?> activeLoader = root.getActiveLoader();
        if (activeLoader != null) {
            exception.setErrorIfMissing(activeLoader.getErrorDescription());
        }
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
        return value != null || exception == null;
    }

    public boolean isInvalid() {
        return exception != null || value == null;
    }

    public boolean test(Predicate<? super T> predicate) {
        return isValid() && predicate.test(value);
    }

    public void ifValid(@NotNull Consumer<? super @NotNull T> action) {
        if (isValid()) {
            action.accept(value);
        } else if (existsInConfig()) {
            collectException();
        }
    }

    public void ifValidOrWarn(@NotNull Consumer<? super @NotNull T> action) {
        if (isValid()) {
            action.accept(value);
        } else if (existsInConfig()) {
            assert exception != null;
            ConfigRoot root = field.getRoot();
            root.collectWarning(exception);
        }
    }

    public void ifValidOrThrow(@NotNull Consumer<? super @NotNull T> action) throws InvalidConfigException {
        if (isValid()) {
            action.accept(value);
        } else if (existsInConfig()) {
            assert exception != null;
            throw exception;
        }
    }

    public void ifValidOrElse(@NotNull Consumer<? super @NotNull T> action, Runnable orElse) {
        if (isValid()) {
            action.accept(value);
        } else if (existsInConfig()) {
            collectException();
            orElse.run();
        }
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    public void throwErrorIfAny() throws InvalidConfigException {
        if (isInvalid() && existsInConfig()) {
            throw exception;
        }
    }

    public void collectErrorIfAny(Consumer<InvalidConfigException> errorCollector) {
        if (isInvalid() && existsInConfig()) {
            errorCollector.accept(exception);
        }
    }

    public @NotNull T orThrow() throws InvalidConfigException {
        if (isInvalid()) {
            throw exception;
        }
        return Objects.requireNonNull(value);
    }

    @Contract("!null -> !null")
    public T withDefault(@Nullable T defaultValue) {
        if (isValid()) {
            return value;
        } else if (existsInConfig()) {
            collectException();
        }
        return defaultValue;
    }

    @Contract("!null -> !null")
    public T withDefaultOrWarn(@Nullable T defaultValue) {
        if (isValid()) {
            return value;
        } else if (existsInConfig()) {
            ConfigRoot root = field.getRoot();
            root.collectWarning(exception);
        }
        return defaultValue;
    }

    @Contract("!null -> !null")
    public T withDefaultOrThrow(@Nullable T defaultValue) throws InvalidConfigException {
        if (isValid()) {
            return value;
        } else if (existsInConfig()) {
            throw exception;
        }
        return defaultValue;
    }

    @Contract("!null, _ -> !null")
    public T withDefaultOrElse(@Nullable T defaultValue, Runnable orElse) {
        if (isValid()) {
            return value;
        } else if (existsInConfig()) {
            collectException();
            orElse.run();
        }
        return defaultValue;
    }

    public @NotNull Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }

    private void collectException() {
        assert exception != null;
        ConfigRoot root = field.getRoot();
        if (warning) {
            root.collectWarning(exception);
        } else {
            root.collectError(exception);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value, exception, existsInConfig);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AbstractOptional<?> that)) return false;
        return existsInConfig == that.existsInConfig && Objects.equals(field, that.field) && Objects.equals(value, that.value) && Objects.equals(exception, that.exception);
    }

}
