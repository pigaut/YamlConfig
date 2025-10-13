package io.github.pigaut.yaml.configurator.convert.deserialize;

import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.util.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Deserializers {

    public static final Deserializer<Boolean> BOOLEAN = ParseUtil::parseBoolean;
    public static final Deserializer<Character> CHARACTER = ParseUtil::parseCharacter;
    public static final Deserializer<String> STRING = string -> string;
    public static final Deserializer<Byte> BYTE = ParseUtil::parseByte;
    public static final Deserializer<Short> SHORT = ParseUtil::parseShort;
    public static final Deserializer<Integer> INTEGER = ParseUtil::parseInteger;
    public static final Deserializer<Long> LONG = ParseUtil::parseLong;
    public static final Deserializer<Float> FLOAT = ParseUtil::parseFloat;
    public static final Deserializer<Double> DOUBLE = ParseUtil::parseDouble;
    public static final Deserializer<Amount> AMOUNT = ParseUtil::parseAmount;
    public static final Deserializer<Ticks> TIME = ParseUtil::parseTime;
    public static final Deserializer<LocalDate> LOCAL_DATE = ParseUtil::parseLocalDate;
    public static final Deserializer<LocalTime> LOCAL_TIME = ParseUtil::parseLocalTime;
    public static final Deserializer<LocalDateTime> LOCAL_DATE_TIME = ParseUtil::parseLocalDateTime;
    public static final Deserializer<BigInteger> BIG_INTEGER = ParseUtil::parseBigInteger;
    public static final Deserializer<BigDecimal> BIG_DECIMAL = ParseUtil::parseBigDecimal;
    public static final Deserializer<File> FILE = ParseUtil::parseFile;
    public static final Deserializer<Locale> LOCALE = ParseUtil::parseLocale;
    public static final Deserializer<UUID> UUID = ParseUtil::parseUUID;
    public static final Deserializer<java.net.URL> URL = ParseUtil::parseURL;

    public static <E extends Enum<E>> Deserializer<E> enumDeserializer(Class<E> classType) {
        return string -> {
            try {
                return Enum.valueOf(classType, CaseFormatter.toConstantCase(string));
            }
            catch (IllegalArgumentException e) {
                final String typeName = CaseFormatter.toTitleCase(CaseFormatter.splitClassName(classType));
                throw new StringParseException("Expected a(n) " + typeName + " but found: '" + string + "'");
            }
        };
    }

    private static final Map<Class<?>, Deserializer<?>> DESERIALIZERS = new HashMap<>();

    static {
        DESERIALIZERS.put(Boolean.class, BOOLEAN);
        DESERIALIZERS.put(boolean.class, BOOLEAN);

        DESERIALIZERS.put(Character.class, CHARACTER);
        DESERIALIZERS.put(char.class, CHARACTER);

        DESERIALIZERS.put(String.class, STRING);

        DESERIALIZERS.put(Byte.class, BYTE);
        DESERIALIZERS.put(byte.class, BYTE);

        DESERIALIZERS.put(Short.class, SHORT);
        DESERIALIZERS.put(short.class, SHORT);

        DESERIALIZERS.put(Integer.class, INTEGER);
        DESERIALIZERS.put(int.class, INTEGER);

        DESERIALIZERS.put(Long.class, LONG);
        DESERIALIZERS.put(long.class, LONG);

        DESERIALIZERS.put(Float.class, FLOAT);
        DESERIALIZERS.put(float.class, FLOAT);

        DESERIALIZERS.put(Double.class, DOUBLE);
        DESERIALIZERS.put(double.class, DOUBLE);

        DESERIALIZERS.put(Amount.class, AMOUNT);
        DESERIALIZERS.put(Ticks.class, TIME);

        DESERIALIZERS.put(BigInteger.class, BIG_INTEGER);
        DESERIALIZERS.put(BigDecimal.class, BIG_DECIMAL);

        DESERIALIZERS.put(LocalDate.class, LOCAL_DATE);
        DESERIALIZERS.put(LocalTime.class, LOCAL_TIME);
        DESERIALIZERS.put(LocalDateTime.class, LOCAL_DATE_TIME);

        DESERIALIZERS.put(File.class, FILE);
        DESERIALIZERS.put(Locale.class, LOCALE);
        DESERIALIZERS.put(UUID.class, UUID);
        DESERIALIZERS.put(URL.class, URL);
    }

    public static <T> Deserializer<T> getByType(Class<T> classType) {
        if (classType.isEnum()) {
            return (Deserializer<T>) enumDeserializer((Class<? extends Enum>) classType);
        }

        if (!DESERIALIZERS.containsKey(classType)) {
            throw new IllegalArgumentException("No deserializer exists for that class type");
        }

        return (Deserializer<T>) DESERIALIZERS.get(classType);
    }

}
