package io.github.pigaut.yaml.configurator;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.mapper.*;
import io.github.pigaut.yaml.parser.deserializer.*;
import io.github.pigaut.yaml.parser.serializer.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class StandardConfigurator extends Configurator {

    public StandardConfigurator() {

        addDeserializer(Byte.class, Deserializers.BYTE);
        addDeserializer(Short.class, Deserializers.SHORT);
        addDeserializer(BigInteger.class, Deserializers.BIG_INTEGER);
        addDeserializer(BigDecimal.class, Deserializers.BIG_DECIMAL);
        addDeserializer(LocalDate.class, Deserializers.LOCAL_DATE);
        addDeserializer(LocalTime.class, Deserializers.LOCAL_TIME);
        addDeserializer(LocalDateTime.class, Deserializers.LOCAL_DATE_TIME);
        addDeserializer(UUID.class, Deserializers.UUID);
        addDeserializer(Locale.class, Deserializers.LOCALE);
        addDeserializer(File.class, Deserializers.FILE);
        addDeserializer(URL.class, Deserializers.URL);

        addSerializer(LocalDate.class, Serializers.LOCAL_DATE);
        addSerializer(LocalTime.class, Serializers.LOCAL_TIME);
        addSerializer(LocalDateTime.class, Serializers.LOCAL_DATE_TIME);
        addSerializer(UUID.class, Serializers.defaultSerializer());
        addSerializer(Locale.class, Serializers.LOCALE);
        addSerializer(File.class, Serializers.defaultSerializer());
        addSerializer(URL.class, Serializers.defaultSerializer());

        addMapper(Map.class, new ConfigSectionMapper());
        addMapper(Iterable.class, new ConfigSequenceMapper());

    }

    protected class ConfigSectionMapper implements SectionMapper<Map> {
        @Override
        public void mapSection(@NotNull ConfigSection section, @NotNull Map mappings) {
            section.clear();
            mappings.forEach((key, value) -> {
                final String keyAsString = String.valueOf(key);
                if (value == null) {
                    section.getSectionOrCreate(keyAsString);
                    return;
                }
                section.set(keyAsString, value);
            });
        }
    }

    protected class ConfigSequenceMapper implements SequenceMapper<Iterable> {
        @Override
        public void mapSection(@NotNull ConfigSection section, @NotNull Iterable elements) {
            section.clear();
            int count = 0;
            for (Object element : elements) {
                section.set(Integer.toString(count++), element);
            }
        }
        @Override
        public void mapSequence(@NotNull ConfigSequence sequence, @NotNull Iterable elements) {
            sequence.clear();
            for (Object element : elements) {
                sequence.add(element);
            }
        }
    }

}
