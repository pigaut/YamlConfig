package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public abstract class ConfigOptional<T> {

    public static <T extends ConfigField> ConfigOptional<T> of(@NotNull T field) {
        return new Set<>(field, field);
    }

    public static <T> ConfigOptional<T> of(@NotNull ConfigField field, @NotNull T value) {
        return new Set<>(field, value);
    }

    public static <T> ConfigOptional<T> empty(@NotNull InvalidConfigurationException exception) {
        return new Empty<>(exception);
    }

    public static <T> ConfigOptional<T> empty(ConfigField field, String cause) {
        return new Empty<>(new InvalidConfigurationException(field, cause));
    }

    public static <T> ConfigOptional<T> empty(ConfigLine line, String cause) {
        return new Empty<>(new InvalidConfigurationException(line, cause));
    }

    public abstract boolean isSet();

    public abstract T value();

    public abstract void ifPresent(Consumer<? super T> action);

    public abstract void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);

    public abstract ConfigOptional<T> filter(Predicate<? super T> predicate, String cause);

    public abstract <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper);

    public abstract <U> ConfigOptional<U> flatMap(Function<? super T, ? extends ConfigOptional<? extends U>> mapper);

    public abstract T orElse(T other);

    public abstract T orElseThrow() throws InvalidConfigurationException;

    private static class Set<T> extends ConfigOptional<T> {

        private final ConfigField field;
        private final T value;

        private Set(@NotNull ConfigField field, @NotNull T value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public boolean isSet() {
            return true;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public void ifPresent(Consumer<? super T> action) {
            action.accept(value);
        }

        @Override
        public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            action.accept(value);
        }

        @Override
        public ConfigOptional<T> filter(Predicate<? super T> predicate, String cause) {
            if (predicate.test(value)) {
                return this;
            }
            return new Empty<>(new InvalidConfigurationException(field, cause));
        }

        @Override
        public <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper) {
            return new Set<>(field, mapper.apply(value));
        }

        public <U> ConfigOptional<U> flatMap(Function<? super T, ? extends ConfigOptional<? extends U>> mapper) {
            Objects.requireNonNull(mapper);
            @SuppressWarnings("unchecked")
            ConfigOptional<U> r = (ConfigOptional<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }

        @Override
        public T orElse(T other) {
            return value;
        }

        @Override
        public T orElseThrow() throws InvalidConfigurationException {
            return value;
        }
    }

    private static class Empty<T> extends ConfigOptional<T> {

        private final InvalidConfigurationException exception;

        private Empty(InvalidConfigurationException exception) {
            this.exception = exception;
        }

        @Override
        public boolean isSet() {
            return false;
        }

        @Override
        public T value() {
            throw new IllegalStateException("Value is not set");
        }

        @Override
        public void ifPresent(Consumer<? super T> action) {
            // do nothing
        }

        @Override
        public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            emptyAction.run();
        }

        @Override
        public ConfigOptional<T> filter(Predicate<? super T> predicate, String cause) {
            return new Empty<>(exception);
        }

        @Override
        public <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper) {
            return new Empty<>(exception);
        }

        @Override
        public <U> ConfigOptional<U> flatMap(Function<? super T, ? extends ConfigOptional<? extends U>> mapper) {
            return new Empty<>(exception);
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public T orElseThrow() throws InvalidConfigurationException {
            throw exception;
        }
    }


    public interface ConfigSupplier<T> {
        T get() throws InvalidConfigurationException;
    }

    public static <T> Optional<T> of(ConfigSupplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (InvalidConfigurationException e) {
            return Optional.empty();
        }
    }

}
