package io.github.pigaut.yamlib.config.parser;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Parser implements Serializer<Object> {

    private final Map<Class<?>, Serializer<?>> serializerByType = new HashMap<>();
    private final Map<Class<?>, Deserializer<?>> deserializerByType = new HashMap<>();

    @Override
    public @NotNull String serialize(@NotNull Object value) throws IllegalArgumentException {
        Preconditions.checkNotNull(value, "Value cannot be null");
        Serializer serializer = getSerializer(value.getClass());

        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for class " + value.getClass().getSimpleName());
        }

        return serializer.serialize(value);
    }

    public Serializer getSerializer(Class<?> classType) {
        if (classType.isEnum()) {
            return Serializer.defaultSerializer();
        }
        return serializerByType.get(classType);
    }

    public <T> Serializer<T> getExactSerializer(Class<T> classType) {
        return (Serializer<T>) serializerByType.get(classType);
    }

    public <T> void registerSerializer(Class<T> classType, Serializer<T> parser) {
        serializerByType.put(classType, parser);
    }

    @NotNull
    public <T> T deserializeOrThrow(Class<T> type, @NotNull String string) throws IllegalArgumentException, DeserializationException {
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(string, "String cannot be null");
        Deserializer<T> deserializer = getExactDeserializer(type);

        if (deserializer == null) {
            throw new IllegalArgumentException("No deserializer found for class " + type.getSimpleName());
        }

        return deserializer.deserialize(string);
    }

    @Nullable
    public <T> T deserialize(Class<T> type, @NotNull String string) throws IllegalArgumentException {
        try {
            return deserializeOrThrow(type, string);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public Deserializer getDeserializer(Class<?> classType) {
        if (classType.isEnum()) {
            return Deserializer.enumDeserializer((Class<? extends Enum>) classType);
        }
        return deserializerByType.get(classType);
    }

    public <T> Deserializer<T> getExactDeserializer(Class<T> classType) {
        if (classType.isEnum()) {
            return Deserializer.enumDeserializer((Class<? extends Enum>) classType);
        }
        return (Deserializer<T>) deserializerByType.get(classType);
    }

    public <T> void registerDeserializer(Class<T> classType, Deserializer<T> parser) {
        deserializerByType.put(classType, parser);
    }

}
