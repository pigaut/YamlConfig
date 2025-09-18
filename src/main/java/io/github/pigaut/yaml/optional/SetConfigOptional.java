package io.github.pigaut.yaml.optional;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class SetConfigOptional<T> implements ConfigOptional<T> {

    private final ConfigField field;
    private final T value;

    SetConfigOptional(@NotNull ConfigField field, @NotNull T value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public boolean isSet() {
        return true;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public @NotNull T value() {
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
    public ConfigOptional<T> filter(Predicate<? super T> predicate, String errorMessage) {
        if (predicate.test(value)) {
            return this;
        }
        InvalidConfigurationException exception = field instanceof ConfigLine line ?
                new InvalidConfigurationException(line, errorMessage) :
                new InvalidConfigurationException(field, errorMessage);


        return new EmptyConfigOptional<>(new InvalidConfigurationException(field, errorMessage), true);
    }

    @Override
    public <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper) {
        return new SetConfigOptional<>(field, mapper.apply(value));
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
    public T throwOrElse(T other) throws InvalidConfigurationException {
        return value;
    }

    @Override
    public @NotNull T orThrow() throws InvalidConfigurationException {
        return value;
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return value();
    }

    @Override
    public Optional<T> asOptional() {
        return Optional.of(value);
    }

}
