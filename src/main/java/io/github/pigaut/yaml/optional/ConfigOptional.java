package io.github.pigaut.yaml.optional;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public interface ConfigOptional<T> {

    static <T extends ConfigField> ConfigOptional<T> of(@NotNull T field) {
        return new SetConfigOptional<>(field, field);
    }

    static <T> ConfigOptional<T> of(@NotNull ConfigField field, @NotNull T value) {
        return new SetConfigOptional<>(field, value);
    }

    static <T> ConfigOptional<T> notSet(ConfigField field, String cause) {
        return new EmptyConfigOptional<>(new InvalidConfigurationException(field, cause), false);
    }

    static <T> ConfigOptional<T> notSet(@NotNull InvalidConfigurationException exception) {
        return new EmptyConfigOptional<>(exception, false);
    }

    static <T> ConfigOptional<T> invalid(ConfigField field, String cause) {
        return new EmptyConfigOptional<>(new InvalidConfigurationException(field, cause), false);
    }

    static <T> ConfigOptional<T> invalid(@NotNull InvalidConfigurationException exception) {
        return new EmptyConfigOptional<>(exception, true);
    }

    boolean isSet();

    boolean isPresent();

    @NotNull
    T value() throws NoSuchElementException;

    void ifPresent(Consumer<? super T> action);

    void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);

    ConfigOptional<T> filter(Predicate<? super T> predicate, String errorMessage);

    <U> ConfigOptional<U> map(Function<? super T, ? extends U> mapper);

    <U> ConfigOptional<U> flatMap(Function<? super T, ? extends ConfigOptional<? extends U>> mapper);

    @NotNull T orThrow() throws InvalidConfigurationException;

    T throwOrElse(T other) throws InvalidConfigurationException;

    T orElse(T other);

    T orElseGet(Supplier<? extends T> other);

    Optional<T> asOptional();

}
