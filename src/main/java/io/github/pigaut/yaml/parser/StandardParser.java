package io.github.pigaut.yaml.parser;

import io.github.pigaut.yaml.parser.deserializer.*;
import io.github.pigaut.yaml.parser.serializer.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class StandardParser extends Parser {

    public StandardParser() {
        addSerializer(Boolean.class, Serializers.defaultSerializer());
        addSerializer(Character.class, Serializers.defaultSerializer());
        addSerializer(String.class, Serializers.defaultSerializer());
        addSerializer(Byte.class, Serializers.defaultSerializer());
        addSerializer(Short.class, Serializers.defaultSerializer());
        addSerializer(Integer.class, Serializers.defaultSerializer());
        addSerializer(Double.class, Serializers.defaultSerializer());
        addSerializer(Long.class, Serializers.defaultSerializer());
        addSerializer(Float.class, Serializers.defaultSerializer());
        addSerializer(BigInteger.class, Serializers.defaultSerializer());
        addSerializer(BigDecimal.class, Serializers.defaultSerializer());
        addSerializer(LocalDate.class, Serializers.LOCAL_DATE);
        addSerializer(LocalTime.class, Serializers.LOCAL_TIME);
        addSerializer(LocalDateTime.class, Serializers.LOCAL_DATE_TIME);
        addSerializer(UUID.class, Serializers.defaultSerializer());
        addSerializer(Locale.class, Serializers.LOCALE);
        addSerializer(File.class, Serializers.defaultSerializer());
        addSerializer(URL.class, Serializers.defaultSerializer());

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
        addDeserializer(LocalTime.class, Deserializers.LOCAL_TIME);
        addDeserializer(LocalDateTime.class, Deserializers.LOCAL_DATE_TIME);
        addDeserializer(UUID.class, Deserializers.UUID);
        addDeserializer(Locale.class, Deserializers.LOCALE);
        addDeserializer(File.class, Deserializers.FILE);
        addDeserializer(URL.class, Deserializers.URL);
    }

}
