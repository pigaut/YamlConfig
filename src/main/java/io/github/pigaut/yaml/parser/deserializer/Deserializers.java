package io.github.pigaut.yaml.parser.deserializer;

import io.github.pigaut.yaml.configurator.parser.*;
import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Deserializers {

    private Deserializers() {}

    public static @Nullable Boolean getBoolean(String value) {
        try {
            return deserializeBoolean(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static boolean deserializeBoolean(String value) throws DeserializationException {
        return BOOLEAN.deserialize(value);
    }

    public static @Nullable Character getCharacter(String value) {
        try {
            return deserializeCharacter(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static char deserializeCharacter(String value) throws DeserializationException {
        return CHARACTER.deserialize(value);
    }

    public static @Nullable String getString(String value) {
        try {
            return deserializeString(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static String deserializeString(String value) throws DeserializationException {
        return STRING.deserialize(value);
    }

    public static @Nullable Byte getByte(String value) {
        try {
            return deserializeByte(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static byte deserializeByte(String value) throws DeserializationException {
        return BYTE.deserialize(value);
    }

    public static @Nullable Short getShort(String value) {
        try {
            return deserializeShort(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static short deserializeShort(String value) throws DeserializationException {
        return SHORT.deserialize(value);
    }

    public static @Nullable Integer getInteger(String value) {
        try {
            return deserializeInteger(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static int deserializeInteger(String value) throws DeserializationException {
        return INTEGER.deserialize(value);
    }

    public static @Nullable Long getLong(String value) {
        try {
            return deserializeLong(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static long deserializeLong(String value) throws DeserializationException {
        return LONG.deserialize(value);
    }

    public static @Nullable Float getFloat(String value) {
        try {
            return deserializeFloat(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static float deserializeFloat(String value) throws DeserializationException {
        return FLOAT.deserialize(value);
    }

    public static @Nullable Double getDouble(String value) {
        try {
            return deserializeDouble(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static double deserializeDouble(String value) throws DeserializationException {
        return DOUBLE.deserialize(value);
    }

    public static @Nullable LocalDate getLocalDate(String value) {
        try {
            return deserializeLocalDate(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static LocalDate deserializeLocalDate(String value) throws DeserializationException {
        return LOCAL_DATE.deserialize(value);
    }

    public static @Nullable LocalTime getLocalTime(String value) {
        try {
            return deserializeLocalTime(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static LocalTime deserializeLocalTime(String value) throws DeserializationException {
        return LOCAL_TIME.deserialize(value);
    }

    public static @Nullable LocalDateTime getLocalDateTime(String value) {
        try {
            return deserializeLocalDateTime(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static LocalDateTime deserializeLocalDateTime(String value) throws DeserializationException {
        return LOCAL_DATE_TIME.deserialize(value);
    }

    public static @Nullable BigInteger getBigInteger(String value) {
        try {
            return deserializeBigInteger(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static BigInteger deserializeBigInteger(String value) throws DeserializationException {
        return BIG_INTEGER.deserialize(value);
    }

    public static @Nullable BigDecimal getBigDecimal(String value) {
        try {
            return deserializeBigDecimal(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static BigDecimal deserializeBigDecimal(String value) throws DeserializationException {
        return BIG_DECIMAL.deserialize(value);
    }

    public static @Nullable File getFile(String value) {
        try {
            return deserializeFile(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static File deserializeFile(String value) throws DeserializationException {
        return FILE.deserialize(value);
    }

    public static @Nullable Locale getLocale(String value) {
        try {
            return deserializeLocale(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static Locale deserializeLocale(String value) throws DeserializationException {
        return LOCALE.deserialize(value);
    }

    public static @Nullable UUID getUUID(String value) {
        try {
            return deserializeUUID(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static UUID deserializeUUID(String value) throws DeserializationException {
        return UUID.deserialize(value);
    }

    public static @Nullable URL getURL(String value) {
        try {
            return deserializeURL(value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static URL deserializeURL(String value) throws DeserializationException {
        return URL.deserialize(value);
    }

    public static <E extends Enum<E>> @Nullable E getEnum(Class<E> type, String value) {
        try {
            return deserializeEnum(type, value);
        } catch (DeserializationException e) {
            return null;
        }
    }

    public static <E extends Enum<E>> E deserializeEnum(Class<E> type, String value) throws DeserializationException {
        return enumDeserializer(type).deserialize(value);
    }

    public static final ConfigDeserializer<Boolean> BOOLEAN = string -> {
        if ("true".equalsIgnoreCase(string)) return true;
        if ("false".equalsIgnoreCase(string)) return false;
        throw new DeserializationException("Expected a boolean but found: '" + string + "'");
    };

    public static final ConfigDeserializer<Character> CHARACTER = string -> {
        if (string.length() == 1) {
            return string.charAt(0);
        }
        throw new DeserializationException("Expected a character but found: '" + string + "'");
    };

    public static final ConfigDeserializer<String> STRING = string -> string;

    public static final ConfigDeserializer<Byte> BYTE = string -> {
        try {
            return Byte.parseByte(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected a byte but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<Short> SHORT = string -> {
        try {
            return Short.parseShort(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected a short but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<Integer> INTEGER = string -> {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected an integer but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<Double> DOUBLE = string -> {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected a double but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<Long> LONG = string -> {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected a long but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<Float> FLOAT = string -> {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected a float but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<LocalDate> LOCAL_DATE = string -> {
        try {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new DeserializationException("Expected a date but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<LocalTime> LOCAL_TIME = string -> {
        try {
            return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            throw new DeserializationException("Expected a time but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<LocalDateTime> LOCAL_DATE_TIME = string -> {
        try {
            return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            throw new DeserializationException("Expected a date and time but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<BigInteger> BIG_INTEGER = string -> {
        try {
            return new BigInteger(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected a big integer but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<BigDecimal> BIG_DECIMAL = string -> {
        try {
            return new BigDecimal(string);
        } catch (NumberFormatException e) {
            throw new DeserializationException("Expected a big decimal but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<File> FILE = File::new;

    public static final ConfigDeserializer<Locale> LOCALE = string -> {
        try {
            return Locale.forLanguageTag(string);
        } catch (Exception e) {
            throw new DeserializationException("Expected a locale but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<UUID> UUID = string -> {
        try {
            return java.util.UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            throw new DeserializationException("Expected a uuid but found: '" + string + "'");
        }
    };

    public static final ConfigDeserializer<URL> URL = string -> {
        try {
            return new URL(string);
        } catch (MalformedURLException e) {
            throw new DeserializationException("Expected a url but found: '" + string + "'");
        }
    };

    public static <E extends Enum<E>> ConfigDeserializer<E> enumDeserializer(Class<E> type) {
        return string -> {
            try {
                return Enum.valueOf(type, StringFormatter.toConstantCase(string));
            } catch (IllegalArgumentException e) {
                throw new DeserializationException("Expected a " + StringFormatter.toSpacedLowerCase(type.getSimpleName())
                        + " but found: '" + string + "'");
            }
        };
    }

}
