package io.github.pigaut.yaml.optional;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class EmptyConfigOptional<T> implements ConfigOptional<T> {

    private final InvalidConfigurationException exception;
    private final boolean existsInConfig;

    EmptyConfigOptional(InvalidConfigurationException exception, boolean existsInConfig) {
        this.exception = exception;
        this.existsInConfig = existsInConfig;
    }

    @Override
    public boolean isSet() {
        return existsInConfig;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public @NotNull T value() {
        throw new NoSuchElementException();
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
    public ConfigOptional<T> filter(Predicate<? super T> predicate, String errorMessage) {
        return this;
    }

    @Override
    public <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper) {
        return new EmptyConfigOptional<>(exception, existsInConfig);
    }

    @Override
    public <U> ConfigOptional<U> flatMap(Function<? super T, ? extends ConfigOptional<? extends U>> mapper) {
        return new EmptyConfigOptional<>(exception, existsInConfig);
    }

    @Override
    public T orElse(T other) {
        return other;
    }

    @Override
    public T throwOrElse(T other) throws InvalidConfigurationException {
        if (isSet()) {
            throw exception;
        }
        return other;
    }

    @Override
    public @NotNull T orThrow() throws InvalidConfigurationException {
        throw exception;
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return other.get();
    }

    @Override
    public Optional<T> asOptional() {
        return Optional.empty();
    }

}
