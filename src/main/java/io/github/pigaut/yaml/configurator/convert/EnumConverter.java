package io.github.pigaut.yaml.configurator.convert;

import io.github.pigaut.yaml.configurator.convert.deserialize.*;
import io.github.pigaut.yaml.configurator.convert.serialize.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class EnumConverter<E extends Enum<E>> implements Converter<E> {

    private final Map<String, E> enumByAlias = new HashMap<>();
    private final Map<E, String> aliasByEnum = new HashMap<>();
    private final Deserializer<E> deserializer;
    private final Serializer<E> serializer;

    public EnumConverter(Class<E> enumClass) {
        this.deserializer = Deserializers.enumDeserializer(enumClass);
        this.serializer = Serializers.defaultSerializer();
    }

    public void addAlias(E enumValue, String... aliases) {
        Preconditions.checkArgument(aliases.length != 0, "Aliases cannot be empty");
        Preconditions.checkNotNull(enumValue, "Value cannot be null");

        for (String alias : aliases) {
            final String enumName = CaseFormatter.toConstantCase(alias);
            enumByAlias.put(enumName, enumValue);
        }
    }

    public void removeAlias(String... aliases) {
        Preconditions.checkArgument(aliases.length != 0, "Aliases cannot be empty");
        for (String alias : aliases) {
            final String enumName = CaseFormatter.toConstantCase(alias);
            enumByAlias.remove(enumName);
        }
    }

    public void addReplacement(E enumValue, String newName) {
        aliasByEnum.put(enumValue, CaseFormatter.toConstantCase(newName));
    }

    public void removeReplacement(E enumValue) {
        aliasByEnum.remove(enumValue);
    }

    @Override
    public E deserialize(String string) throws StringParseException {
        final String enumName = CaseFormatter.toConstantCase(string);
        if (enumByAlias.containsKey(enumName)) {
            return enumByAlias.get(enumName);
        }
        return deserializer.deserialize(string);
    }

    @Override
    public @NotNull String serialize(@NotNull E value) {
        if (aliasByEnum.containsKey(value)) {
            return aliasByEnum.get(value);
        }
        return serializer.serialize(value);
    }

}
