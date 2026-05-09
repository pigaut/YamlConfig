package io.github.pigaut.yaml.configurator;

import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.amount.config.*;
import io.github.pigaut.yaml.configurator.convert.deserialize.*;
import io.github.pigaut.yaml.configurator.convert.serialize.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.util.*;
import org.snakeyaml.engine.v2.nodes.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class StandardConfigurator extends Configurator {

    public StandardConfigurator() {
        addDeserializer(Boolean.class, Deserializers.BOOLEAN);
        addDeserializer(Character.class, Deserializers.CHARACTER);
        addDeserializer(String.class, Deserializers.STRING);
        addDeserializer(Byte.class, Deserializers.BYTE);
        addDeserializer(Short.class, Deserializers.SHORT);
        addDeserializer(Integer.class, Deserializers.INTEGER);
        addDeserializer(Long.class, Deserializers.LONG);
        addDeserializer(Float.class, Deserializers.FLOAT);
        addDeserializer(Double.class, Deserializers.DOUBLE);
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

        addDeserializer(Ticks.class, Deserializers.TIME);
        addSerializer(Ticks.class, Serializers.defaultSerializer());

        addLoader(Amount.class, new AmountLoader());
        addMapper(FixedAmount.class, new FixedAmountMapper());
        addMapper(RangedAmount.class, new RangedAmountMapper());
        addMapper(RandomAmount.class, new RandomAmountMapper());

        addMapper(Map.class, new MapMapper());
        addMapper(Iterable.class, new IterableMapper());

        addMapper(MappingNode.class, new MappingNodeMapper());
        addMapper(SequenceNode.class, new SequenceNodeMapper());
        addMapper(ScalarNode.class, new ScalarNodeMapper());
        addMapper(NodeTuple.class, new NodeTupleMapper());

        addLoader(MappingNode.class, new MappingNodeLoader());
        addLoader(SequenceNode.class, new SequenceNodeLoader());
        addLoader(ScalarNode.class, new ScalarNodeLoader());
    }

}
