package io.github.pigaut.yaml.configurator;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.amount.config.*;
import io.github.pigaut.yaml.configurator.deserialize.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.configurator.serialize.*;
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
        addSerializer(LocalDate.class, Serializers.LOCAL_DATE);

        addDeserializer(LocalTime.class, Deserializers.LOCAL_TIME);
        addSerializer(LocalTime.class, Serializers.LOCAL_TIME);

        addDeserializer(LocalDateTime.class, Deserializers.LOCAL_DATE_TIME);
        addSerializer(LocalDateTime.class, Serializers.LOCAL_DATE_TIME);

        addDeserializer(UUID.class, Deserializers.UUID);
        addSerializer(UUID.class, Serializers.defaultSerializer());

        addDeserializer(Locale.class, Deserializers.LOCALE);
        addSerializer(Locale.class, Serializers.LOCALE);

        addDeserializer(File.class, Deserializers.FILE);
        addSerializer(File.class, Serializers.defaultSerializer());

        addDeserializer(URL.class, Deserializers.URL);
        addSerializer(URL.class, Serializers.defaultSerializer());

        addLoader(Amount.class, new AmountLoader());
        addMapper(FixedAmount.class, new FixedAmountMapper());
        addMapper(RangedAmount.class, new RangedAmountMapper());
        addMapper(CasualAmount.class, new CasualAmountMapper());

        addMapper(Map.class, new ConfigSectionMapper());
        addMapper(Iterable.class, new ConfigSequenceMapper());
    }

    protected static class ConfigSectionMapper implements ConfigMapper.Section<Map> {
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

    protected static class ConfigSequenceMapper implements ConfigMapper.Sequence<Iterable> {
        @Override
        public void mapSection(@NotNull ConfigSection section, @NotNull Iterable elements) {
            section.clear();
            int count = 0;
            for (Object element : elements) {
                final String indexAsKey = Integer.toString(count++);
                section.set(indexAsKey, element);
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
