package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class ConfigOptional<T> {

    private final @Nullable T value;
    private InvalidConfigurationException exception;

    public ConfigOptional(@Nullable T value, @NotNull InvalidConfigurationException exception) {
        this.value = value;
        this.exception = exception;
    }

    public static <T> ConfigOptional<T> empty(InvalidConfigurationException exception) {
        return new ConfigOptional<>(null, exception);
    }

    public boolean isSet() {
        return value != null;
    }

    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (value != null) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    public ConfigOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isSet()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty(exception);
        }
    }

    public <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper) {
        Preconditions.checkNotNull(mapper, "Mapper cannot be null.");
        if (!isSet()) {
            return empty(exception);
        }
        else {
            return new ConfigOptional<>(mapper.apply(value), exception);
        }
    }

    @NotNull
    public T orElseThrow() throws InvalidConfigurationException {
        if (value != null) {
            return value;
        }
        throw exception;
    }

    @Nullable
    public T orElseNull() {
        return value;
    }

    public T orElse(T other) {
        return value != null ? value : other;
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
