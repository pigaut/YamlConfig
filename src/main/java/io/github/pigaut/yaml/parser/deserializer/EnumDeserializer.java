package io.github.pigaut.yaml.parser.deserializer;

import io.github.pigaut.yaml.configurator.parser.*;
import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;

import javax.print.*;

public class EnumDeserializer<E extends Enum<E>> implements ConfigDeserializer<E> {

    private final Class<E> type;

    public EnumDeserializer(Class<E> type) {
        this.type = type;
    }

    @Override
    public @NotNull E deserialize(@NotNull String stringToDeserialize) throws DeserializationException {
        try {
            return Enum.valueOf(type, StringFormatter.toConstantCase(stringToDeserialize));
        } catch (IllegalArgumentException e) {
            final String typeName = StringFormatter.toTitleCase(StringFormatter.splitClassName(type));
            throw new DeserializationException("Expected a " + typeName + " but found: '" + stringToDeserialize + "'");
        }
    }

    public static <E extends Enum<E>> @NotNull E deserialize(Class<E> type, String value) throws DeserializationException {
        final EnumDeserializer<E> deserializer = new EnumDeserializer<>(type);
        return deserializer.deserialize(value);
    }

    public static <E extends Enum<E>> @Nullable E deserializeOrNull(Class<E> type, String value) {
        try {
            return deserialize(type, value);
        } catch (DeserializationException e) {
            return null;
        }
    }

}
