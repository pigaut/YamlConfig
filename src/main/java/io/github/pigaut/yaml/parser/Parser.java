package io.github.pigaut.yaml.parser;

import io.github.pigaut.yaml.parser.deserializer.*;
import io.github.pigaut.yaml.parser.serializer.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Parser {

    private final Map<Class<?>, Serializer<?>> serializersByType = new HashMap<>();
    private final Map<Class<?>, Deserializer<?>> deserializersByType = new HashMap<>();

    public <T> @NotNull String serialize(@NotNull T value) {
        @SuppressWarnings("unchecked")
        Serializer<? super T> serializer = (Serializer<? super T>) getSerializer(value.getClass());

        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for class: " + value.getClass().getSimpleName());
        }

        return serializer.serialize(value);
    }

    public <T> Serializer<? super T> getSerializer(Class<T> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return Serializers.defaultSerializer();
        }

        @SuppressWarnings("unchecked")
        Serializer<T> serializer = (Serializer<T>) serializersByType.get(type);

        if (serializer != null) {
            return serializer;
        }

        for (Class<?> serializerType : serializersByType.keySet()) {
            if (serializerType.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                Serializer<? super T> parentSerializer = (Serializer<? super T>) serializersByType.get(serializerType);
                return parentSerializer;
            }
        }

        return null;
    }

    public <T> void addSerializer(@NotNull Class<T> type, @NotNull Serializer<T> serializer) {
        serializersByType.put(type, serializer);
    }

    public <T> @NotNull T deserialize(@NotNull Class<T> type, @NotNull String value) throws DeserializationException {
        Deserializer<? extends T> deserializer = getDeserializer(type);

        if (deserializer == null) {
            throw new IllegalArgumentException("No deserializer found for class: " + type.getSimpleName());
        }

        return deserializer.deserialize(value);
    }

    public <T> Deserializer<? extends T> getDeserializer(Class<T> type) {
        if (Enum.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            Class enumType = type;
            return Deserializers.enumDeserializer(enumType);
        }

        @SuppressWarnings("uncheked")
        Deserializer<T> deserializer = (Deserializer<T>) deserializersByType.get(type);
        return deserializer;
    }

    public <T> void addDeserializer(@NotNull Class<T> type, @NotNull Deserializer<T> deserializer) {
        deserializersByType.put(type, deserializer);
    }

}
