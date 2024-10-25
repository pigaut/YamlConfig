package io.github.pigaut.yamlib.config.parser;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public interface Deserializer<T> {

    @NotNull T deserialize(@NotNull String string) throws DeserializationException;

    default T deserializeOrNull(@NotNull String value) {
        try {
            return deserialize(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    Deserializer<Boolean> BOOLEAN = string -> {
        if ("true".equalsIgnoreCase(string)) return true;
        if ("false".equalsIgnoreCase(string)) return false;
        throw new DeserializationException("could not parse " + string + " as a boolean");
    };

    Deserializer<Character> CHARACTER = string -> {
        if (string.length() == 1) {
            return string.charAt(0);
        }
        throw new DeserializationException("could not parse " + string + " as a character");
    };

    Deserializer<Byte> BYTE = string -> {
        try {
            return Byte.parseByte(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as a byte");
        }
    };

    Deserializer<Short> SHORT = string -> {
        try {
            return Short.parseShort(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as a short");
        }
    };

    Deserializer<Integer> INTEGER = string -> {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as an integer");
        }
    };

    Deserializer<Double> DOUBLE = string -> {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as a double");
        }
    };

    Deserializer<Long> LONG = string -> {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as a long");
        }
    };

    Deserializer<Float> FLOAT = string -> {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as a float");
        }
    };

    static <E extends Enum<E>> Deserializer<E> enumDeserializer(Class<E> type) {
        return string -> {
            try {
                return Enum.valueOf(type, StringFormatter.CONSTANT.format(string));
            } catch (IllegalArgumentException e) {
                throw new DeserializationException("could not parse " + string + " as a " + type.getSimpleName());
            }
        };
    }

    Deserializer<LocalDate> LOCAL_DATE = string -> {
        try {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new DeserializationException("could not parse " + string + " as a LocalDate");
        }
    };

    Deserializer<LocalTime> LOCAL_TIME = string -> {
        try {
            return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            throw new DeserializationException("could not parse " + string + " as a LocalTime");
        }
    };

    Deserializer<LocalDateTime> LOCAL_DATE_TIME = string -> {
        try {
            return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            throw new DeserializationException("could not parse " + string + " as a LocalDateTime");
        }
    };

    Deserializer<BigInteger> BIG_INTEGER = string -> {
        try {
            return new BigInteger(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as a BigInteger");
        }
    };

    Deserializer<BigDecimal> BIG_DECIMAL = string -> {
        try {
            return new BigDecimal(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("could not parse " + string + " as a BigDecimal");
        }
    };

    Deserializer<File> FILE = File::new;

    Deserializer<Locale> LOCALE = string -> {
        try {
            return Locale.forLanguageTag(string);
        } catch (Exception e) {
            throw new DeserializationException("could not parse " + string + " as a Locale");
        }
    };

    Deserializer<UUID> UUID = string -> {
        try {
            return java.util.UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            throw new DeserializationException("could not parse " + string + " as an UUID");
        }
    };

    Deserializer<URL> URL = string -> {
        try {
            return new URL(string);
        } catch (MalformedURLException e) {
            throw new DeserializationException("could not parse " + string + " as an URL");
        }
    };

}
