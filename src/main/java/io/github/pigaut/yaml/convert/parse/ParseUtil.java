package io.github.pigaut.yaml.convert.parse;

import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;

public class ParseUtil {

    private ParseUtil() {}

    public static Object parseAsScalar(String string) {
        final Boolean bool = parseBooleanOrNull(string);
        if (bool != null) {
            return bool;
        }

        final Integer integerNumber = parseIntegerOrNull(string);
        if (integerNumber != null) {
            return integerNumber;
        }

        final Double doubleNumber = parseDoubleOrNull(string);
        if (doubleNumber != null) {
            return doubleNumber;
        }

        return string;
    }

    public static List<Object> parseAllAsScalars(String... strings) {
        final List<Object> deserializedList = new ArrayList<>();
        for (String string : strings) {
            deserializedList.add(parseAsScalar(string));
        }
        return deserializedList;
    }

    public static List<Object> parseAllAsScalars(List<String> stringList) {
        final List<Object> deserializedList = new ArrayList<>();
        for (String string : stringList) {
            deserializedList.add(parseAsScalar(string));
        }
        return deserializedList;
    }

    public static Boolean parseBooleanOrNull(String string) {
        try {
            return parseBoolean(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static boolean parseBoolean(String string) throws StringParseException {
        if (string.equalsIgnoreCase("true")) {
            return true;
        }

        if (string.equalsIgnoreCase("false")) {
            return false;
        }

        throw new StringParseException("Expected a boolean but found: '" + string + "'");
    }

    public static Character parseCharacterOrNull(String string) {
        try {
            return parseCharacter(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static char parseCharacter(String string) throws StringParseException {
        if (string.length() == 1) {
            return string.charAt(0);
        }
        throw new StringParseException("Expected a character but found: '" + string + "'");
    }

    public static Byte parseByteOrNull(String string) {
        try {
            return parseByte(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static byte parseByte(String string) throws StringParseException {
        try {
            return Byte.parseByte(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected a byte but found: '" + string + "'");
        }
    }

    public static Short parseShortOrNull(String string) {
        try {
            return parseShort(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static short parseShort(String string) throws StringParseException {
        try {
            return Short.parseShort(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected a short but found: '" + string + "'");
        }
    }

    public static @Nullable Integer parseIntegerOrNull(String string) {
        try {
            return parseInteger(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static int parseInteger(String string) throws StringParseException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected an integer but found: '" + string + "'");
        }
    }

    public static @Nullable Long parseLongOrNull(String string) {
        try {
            return parseLong(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static long parseLong(String string) throws StringParseException {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected a long but found: '" + string + "'");
        }
    }

    public static @Nullable Float parseFloatOrNull(String string) {
        try {
            return parseFloat(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static float parseFloat(String string) throws StringParseException {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected a float but found: '" + string + "'");
        }
    }

    public static @Nullable Double parseDoubleOrNull(String string) {
        try {
            return parseDouble(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static double parseDouble(String string) throws StringParseException {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected a double but found: '" + string + "'");
        }
    }

    public static @Nullable LocalDate parseLocalDateOrNull(String string) {
        try {
            return parseLocalDate(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static LocalDate parseLocalDate(String string) throws StringParseException {
        try {
            return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new StringParseException("Expected a date but found: '" + string + "'");
        }
    }

    public static @NotNull Amount parseAmount(String string) throws StringParseException {
        string = string.trim();

        if (string.endsWith("+")) {
            Double amount = parseDoubleOrNull(string.replace("+", ""));
            if (amount == null) {
                throw new StringParseException("Expected an inequality amount but found: '" + string + "'");
            }
            return InequalityAmount.greaterThanOrEqualTo(amount);
        }
        else if (string.endsWith("-")) {
            Double amount = parseDoubleOrNull(string.replace("-", ""));
            if (amount == null) {
                throw new StringParseException("Expected an inequality amount but found: '" + string + "'");
            }
            return InequalityAmount.lessThanOrEqualTo(amount);
        }

        final Double value = parseDoubleOrNull(string);
        if (value != null) {
            return Amount.fixed(value);
        }

        if (string.contains("-")) {
            final String[] range = string.split("-");
            if (range.length != 2) {
                throw new StringParseException("Expected a number range but found: '" + string + "'");
            }

            final Double min = parseDoubleOrNull(range[0]);
            final Double max = parseDoubleOrNull(range[1]);

            if (min == null || max == null) {
                throw new StringParseException("Expected a number range but found: '" + string + "'");
            }

            if (min >= max) {
                throw new StringParseException("Number range's min value must be less than max value");
            }

            return Amount.between(min, max);
        }

        else if (string.contains(";")) {
            List<Double> values = new ArrayList<>();
            for (String unparsedValue : Patterns.SEMICOLON_SEPARATED.split(string)) {
                values.add(ParseUtil.parseDouble(unparsedValue));
            }
            return Amount.random(values);
        }

        throw new StringParseException("Expected an amount but found: '" + string + "'");
    }

    public static Amount parseAmountOrNull(String string) {
        try {
            return parseAmount(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(tick|ticks|h|hour|hours|m|min|mins|minute|minutes|s|sec|secs|second|seconds)?");

    public static Ticks parseTime(String string) throws StringParseException {
        string = string.trim().toLowerCase();

        if (string.matches("\\d+")) {
            int ticks = Integer.parseInt(string);
            if (ticks < 0) {
                throw new StringParseException("Time values must be positive");
            }
            return new Ticks(ticks);
        }

        Matcher matcher = TIME_PATTERN.matcher(string);

        int totalTicks = 0;
        while (matcher.find()) {
            double value = Double.parseDouble(matcher.group(1));
            if (value < 0) {
                throw new StringParseException("Time values must be positive");
            }
            String unit = matcher.group(2);

            if (unit == null || unit.equals("tick") || unit.equals("ticks")) {
                totalTicks += (int) value;
            }
            else if (unit.equals("s") || unit.equals("sec") || unit.equals("secs") || unit.equals("second") || unit.equals("seconds")) {
                totalTicks += (int) (value * 20);
            }
            else if (unit.equals("m") || unit.equals("min") || unit.equals("mins") || unit.equals("minute") || unit.equals("minutes")) {
                totalTicks += (int) (value * 1200);
            }
            else if (unit.equals("h") || unit.equals("hour") || unit.equals("hours")) {
                totalTicks += (int) (value * 72000);
            }
            else {
                throw new StringParseException("Expected a time unit but found: '" + unit + "'");
            }
        }

        return new Ticks(totalTicks);
    }

    public static @Nullable Ticks parseTicksOrNull(String string) {
        try {
            return parseTime(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static @Nullable LocalTime parseLocalTimeOrNull(String string) {
        try {
            return parseLocalTime(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static LocalTime parseLocalTime(String string) throws StringParseException {
        try {
            return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            throw new StringParseException("Expected a time but found: '" + string + "'");
        }
    }

    public static @Nullable LocalDateTime parseLocalDateTimeOrNull(String string) {
        try {
            return parseLocalDateTime(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static LocalDateTime parseLocalDateTime(String string) throws StringParseException {
        try {
            return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm[:ss]"));
        } catch (DateTimeParseException e) {
            throw new StringParseException("Expected a date and time but found: '" + string + "'");
        }
    }

    public static @Nullable BigInteger parseBigIntegerOrNull(String string) {
        try {
            return parseBigInteger(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static BigInteger parseBigInteger(String string) throws StringParseException {
        try {
            return new BigInteger(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected a big integer but found: '" + string + "'");
        }
    }

    public static @Nullable BigDecimal parseBigDecimalOrNull(String string) {
        try {
            return parseBigDecimal(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static BigDecimal parseBigDecimal(String string) throws StringParseException {
        try {
            return new BigDecimal(string);
        } catch (NumberFormatException e) {
            throw new StringParseException("Expected a big decimal but found: '" + string + "'");
        }
    }

    public static @Nullable File parseFileOrNull(String string) {
        try {
            return parseFile(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static File parseFile(String string) throws StringParseException {
        return new File(string);
    }

    public static @Nullable Locale parseLocaleOrNull(String string) {
        try {
            return parseLocale(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static Locale parseLocale(String string) throws StringParseException {
        try {
            return Locale.forLanguageTag(string);
        } catch (Exception e) {
            throw new StringParseException("Expected a locale but found: '" + string + "'");
        }
    }

    public static @Nullable UUID parseUUIDOrNull(String string) {
        try {
            return parseUUID(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static UUID parseUUID(String string) throws StringParseException {
        try {
            return java.util.UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            throw new StringParseException("Expected a uuid but found: '" + string + "'");
        }
    }

    public static @Nullable URL parseURLOrNull(String string) {
        try {
            return parseURL(string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static URL parseURL(String string) throws StringParseException {
        try {
            return new URL(string);
        } catch (MalformedURLException e) {
            throw new StringParseException("Expected a url but found: '" + string + "'");
        }
    }

    public static <E extends Enum<E>> @Nullable E parseEnumOrNull(Class<E> classType, String string) {
        try {
            return parseEnum(classType, string);
        } catch (StringParseException e) {
            return null;
        }
    }

    public static <E extends Enum<E>> E parseEnum(Class<E> classType, String string) throws StringParseException {
        return enumParser(classType).parse(string);
    }

    public static <E extends Enum<E>> Parser<E> enumParser(Class<E> classType) {
        return string -> {
            try {
                return Enum.valueOf(classType, CaseFormatter.toConstantCase(string));
            } catch (IllegalArgumentException e) {
                final String typeName = CaseFormatter.toTitleCase(CaseFormatter.splitClassName(classType));
                throw new StringParseException("Expected a " + typeName + " but found: '" + string + "'");
            }
        };
    }
    
}
