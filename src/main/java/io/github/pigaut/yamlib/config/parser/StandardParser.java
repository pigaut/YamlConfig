package io.github.pigaut.yamlib.config.parser;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class StandardParser extends Parser {

    public StandardParser() {
        registerSerializer(Boolean.class, Serializer.defaultSerializer());
        registerSerializer(Character.class, Serializer.defaultSerializer());
        registerSerializer(String.class, Serializer.defaultSerializer());
        registerSerializer(Byte.class, Serializer.defaultSerializer());
        registerSerializer(Short.class, Serializer.defaultSerializer());
        registerSerializer(Integer.class, Serializer.defaultSerializer());
        registerSerializer(Double.class, Serializer.defaultSerializer());
        registerSerializer(Long.class, Serializer.defaultSerializer());
        registerSerializer(Float.class, Serializer.defaultSerializer());
        registerSerializer(BigInteger.class, Serializer.defaultSerializer());
        registerSerializer(BigDecimal.class, Serializer.defaultSerializer());

        registerSerializer(LocalDate.class, Serializer.LOCAL_DATE);
        registerSerializer(LocalTime.class, Serializer.LOCAL_TIME);
        registerSerializer(LocalDateTime.class, Serializer.LOCAL_DATE_TIME);
        registerSerializer(UUID.class, Serializer.defaultSerializer());
        registerSerializer(Locale.class, Serializer.LOCALE);
        registerSerializer(File.class, Serializer.defaultSerializer());
        registerSerializer(URL.class, Serializer.defaultSerializer());

        registerDeserializer(Boolean.class, Deserializer.BOOLEAN);
        registerDeserializer(Character.class, Deserializer.CHARACTER);
        registerDeserializer(String.class, string -> string);
        registerDeserializer(Byte.class, Deserializer.BYTE);
        registerDeserializer(Short.class, Deserializer.SHORT);
        registerDeserializer(Integer.class, Deserializer.INTEGER);
        registerDeserializer(Long.class, Deserializer.LONG);
        registerDeserializer(Float.class, Deserializer.FLOAT);
        registerDeserializer(Double.class, Deserializer.DOUBLE);
        registerDeserializer(BigInteger.class, Deserializer.BIG_INTEGER);
        registerDeserializer(BigDecimal.class, Deserializer.BIG_DECIMAL);

        registerDeserializer(LocalDate.class, Deserializer.LOCAL_DATE);
        registerDeserializer(LocalTime.class, Deserializer.LOCAL_TIME);
        registerDeserializer(LocalDateTime.class, Deserializer.LOCAL_DATE_TIME);
        registerDeserializer(UUID.class, Deserializer.UUID);
        registerDeserializer(Locale.class, Deserializer.LOCALE);
        registerDeserializer(File.class, Deserializer.FILE);
        registerDeserializer(URL.class, Deserializer.URL);
    }

}
