package io.github.pigaut.yaml.convert.parse;

import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.util.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Parsers {

    public static final Parser<Boolean> BOOLEAN = ParseUtil::parseBoolean;
    public static final Parser<Character> CHARACTER = ParseUtil::parseCharacter;
    public static final Parser<String> STRING = string -> string;
    public static final Parser<Byte> BYTE = ParseUtil::parseByte;
    public static final Parser<Short> SHORT = ParseUtil::parseShort;
    public static final Parser<Integer> INTEGER = ParseUtil::parseInteger;
    public static final Parser<Long> LONG = ParseUtil::parseLong;
    public static final Parser<Float> FLOAT = ParseUtil::parseFloat;
    public static final Parser<Double> DOUBLE = ParseUtil::parseDouble;
    public static final Parser<Amount> AMOUNT = ParseUtil::parseAmount;
    public static final Parser<Ticks> TIME = ParseUtil::parseTime;
    public static final Parser<LocalDate> LOCAL_DATE = ParseUtil::parseLocalDate;
    public static final Parser<LocalTime> LOCAL_TIME = ParseUtil::parseLocalTime;
    public static final Parser<LocalDateTime> LOCAL_DATE_TIME = ParseUtil::parseLocalDateTime;
    public static final Parser<BigInteger> BIG_INTEGER = ParseUtil::parseBigInteger;
    public static final Parser<BigDecimal> BIG_DECIMAL = ParseUtil::parseBigDecimal;
    public static final Parser<File> FILE = ParseUtil::parseFile;
    public static final Parser<Locale> LOCALE = ParseUtil::parseLocale;
    public static final Parser<UUID> UUID = ParseUtil::parseUUID;
    public static final Parser<URL> URL = ParseUtil::parseURL;

    public static <E extends Enum<E>> Parser<E> enumParser(Class<E> classType) {
        return string -> {
            try {
                return Enum.valueOf(classType, CaseFormatter.toConstantCase(string));
            } catch (IllegalArgumentException e) {
                final String typeName = CaseFormatter.toTitleCase(CaseFormatter.splitClassName(classType));
                throw new StringParseException("Expected a(n) " + typeName + " but found: '" + string + "'");
            }
        };
    }

    private static final Map<Class<?>, Parser<?>> PARSERS = new HashMap<>();

    static {
        PARSERS.put(Boolean.class, BOOLEAN);
        PARSERS.put(boolean.class, BOOLEAN);

        PARSERS.put(Character.class, CHARACTER);
        PARSERS.put(char.class, CHARACTER);

        PARSERS.put(String.class, STRING);

        PARSERS.put(Byte.class, BYTE);
        PARSERS.put(byte.class, BYTE);

        PARSERS.put(Short.class, SHORT);
        PARSERS.put(short.class, SHORT);

        PARSERS.put(Integer.class, INTEGER);
        PARSERS.put(int.class, INTEGER);

        PARSERS.put(Long.class, LONG);
        PARSERS.put(long.class, LONG);

        PARSERS.put(Float.class, FLOAT);
        PARSERS.put(float.class, FLOAT);

        PARSERS.put(Double.class, DOUBLE);
        PARSERS.put(double.class, DOUBLE);

        PARSERS.put(Amount.class, AMOUNT);
        PARSERS.put(Ticks.class, TIME);

        PARSERS.put(BigInteger.class, BIG_INTEGER);
        PARSERS.put(BigDecimal.class, BIG_DECIMAL);

        PARSERS.put(LocalDate.class, LOCAL_DATE);
        PARSERS.put(LocalTime.class, LOCAL_TIME);
        PARSERS.put(LocalDateTime.class, LOCAL_DATE_TIME);

        PARSERS.put(File.class, FILE);
        PARSERS.put(Locale.class, LOCALE);
        PARSERS.put(UUID.class, UUID);
        PARSERS.put(java.net.URL.class, URL);
    }

    @SuppressWarnings("unchecked")
    public static <T> Parser<T> getByType(Class<T> classType) {
        if (classType.isEnum()) {
            return (Parser<T>) enumParser((Class<? extends Enum>) classType);
        }

        if (!PARSERS.containsKey(classType)) {
            throw new IllegalArgumentException("No parser exists for that class type");
        }

        return (Parser<T>) PARSERS.get(classType);
    }

}
