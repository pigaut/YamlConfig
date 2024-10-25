package io.github.pigaut.yamlib.factory;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.config.parser.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;

import java.math.*;
import java.util.function.*;

public interface Factory<T> {

    @FunctionalInterface
    interface EmptyFactory<T> extends Factory<T> {
        @NotNull
        T create();
    }

    static <T> Factory<T> from(EmptyFactory<T> emptyFactory) {
        return emptyFactory;
    }

    @FunctionalInterface
    interface BooleanFactory<T> extends Factory<T> {
        @NotNull
        T createFromBoolean(boolean value);
    }

    static <T> Factory<T> fromBool(BooleanFactory<T> booleanFactory) {
        return booleanFactory;
    }

    @FunctionalInterface
    interface CharFactory<T> extends Factory<T> {
        @NotNull
        T createFromCharacter(char value);
    }

    static <T> Factory<T> fromChar(CharFactory<T> charFactory) {
        return charFactory;
    }

    @FunctionalInterface
    interface StringFactory<T> extends Factory<T> {
        @NotNull
        T createFromString(@NotNull String value);
    }

    static <T> Factory<T> fromString(StringFactory<T> stringFactory) {
        return stringFactory;
    }

    static <K, T> Factory<T> fromString(Deserializer<K> deserializer, Function<K, T> function) {
        return fromString((string) -> {
            K key = deserializer.deserializeOrNull(string);
            return key != null ? function.apply(key) : null;
        });
    }

    static <T> Factory<T> fromParser(Deserializer<T> deserializer) {
        return fromString(deserializer::deserializeOrNull);
    }

    @FunctionalInterface
    interface IntFactory<T> extends Factory<T> {
        @NotNull
        T createFromInteger(int value);
    }

    static <T> Factory<T> fromInt(IntFactory<T> factory) {
        return factory;
    }

    @FunctionalInterface
    interface LongFactory<T> extends Factory<T> {
        @NotNull
        T createFromLong(long value);
    }

    static <T> Factory<T> fromLong(LongFactory<T> factory) {
        return factory;
    }

    @FunctionalInterface
    interface DoubleFactory<T> extends Factory<T> {
        @NotNull
        T createFromDouble(double value);
    }

    static <T> Factory<T> fromDouble(DoubleFactory<T> factory) {
        return factory;
    }

    @FunctionalInterface
    interface FloatFactory<T> extends Factory<T> {
        @NotNull
        T createFromFloat(float value);
    }

    static <T> Factory<T> fromFloat(FloatFactory<T> factory) {
        return factory;
    }

    @FunctionalInterface
    interface BigIntegerFactory<T> extends Factory<T> {
        @NotNull
        T createFromBigInteger(@NotNull BigInteger value);
    }

    static <T> Factory<T> fromBigInteger(BigIntegerFactory<T> factory) {
        return factory;
    }

    @FunctionalInterface
    interface BigDecimalFactory<T> extends Factory<T> {
        @NotNull
        T createFromBigDecimal(@NotNull BigDecimal value);
    }

    static <T> Factory<T> fromBigDecimal(BigDecimalFactory<T> factory) {
        return factory;
    }

    @FunctionalInterface
    interface SectionFactory<T> extends Factory<T> {
        @NotNull
        T createFromSection(@NotNull ConfigSection section);
    }

    static <T> Factory<T> fromSection(SectionFactory<T> factory) {
        return factory;
    }

    @NotNull
    default T create() {
        throw new UnsupportedOperationException("create() is not supported.");
    }

    @NotNull
    default T createFromBoolean(boolean value) {
        throw new UnsupportedOperationException("createFromBoolean(boolean) is not supported.");
    }

    @NotNull
    default T createFromCharacter(char value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromCharacter(char) is not supported.");
    }

    @NotNull
    default T createFromString(@NotNull String value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromString(String) is not supported.");
    }

    @NotNull
    default T createFromInteger(int value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromInteger(int) is not supported.");
    }

    @NotNull
    default T createFromLong(long value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromLong(long) is not supported.");
    }

    @NotNull
    default T createFromDouble(double value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromDouble(double) is not supported.");
    }

    @NotNull
    default T createFromFloat(float value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromFloat(float) is not supported.");
    }

    @NotNull
    default T createFromBigInteger(@NotNull BigInteger value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromBigInteger(BigInteger) is not supported.");
    }

    @NotNull
    default T createFromBigDecimal(@NotNull BigDecimal value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createFromBigDecimal(BigDecimal) is not supported.");
    }

    @NotNull
    default T createFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
        throw new UnsupportedOperationException("createFromSection(ConfigSection) is not supported.");
    }

    @NotNull
    default T create(@NotNull Object obj) throws IllegalArgumentException {
        Preconditions.checkNotNull(obj, "Object cannot be null");
        if (obj instanceof Boolean) {
            return createFromBoolean((boolean) obj);
        } else if (obj instanceof Character) {
            return createFromCharacter((char) obj);
        } else if (obj instanceof String) {
            return createFromString((String) obj);
        } else if (obj instanceof Integer) {
            return createFromInteger((int) obj);
        } else if (obj instanceof Long) {
            return createFromLong((long) obj);
        } else if (obj instanceof Double) {
            return createFromDouble((double) obj);
        } else if (obj instanceof Float) {
            return createFromFloat((float) obj);
        } else if (obj instanceof BigInteger) {
            return createFromBigInteger((BigInteger) obj);
        } else if (obj instanceof BigDecimal) {
            return createFromBigDecimal((BigDecimal) obj);
        } else if (obj instanceof ConfigSection) {
            return createFromSection((ConfigSection) obj);
        } else {
            throw new IllegalArgumentException("Cannot create object from type: " + obj.getClass().getSimpleName());
        }
    }

}
