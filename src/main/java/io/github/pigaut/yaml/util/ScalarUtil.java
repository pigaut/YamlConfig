package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.convert.parse.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.scanner.*;

import java.math.*;
import java.util.*;
import java.util.regex.*;

public class ScalarUtil {

    private static final Pattern INT_PATTERN =
            Pattern.compile("^(0|[1-9][0-9]*)$");

    private static final Pattern FLOAT_PATTERN =
            Pattern.compile("^(\\.[0-9]+|(0|[1-9][0-9]*)(\\.[0-9]*)?)([eE][-+]?[0-9]+)?$");

    private static final Pattern BOOL_PATTERN =
            Pattern.compile("^(true|false)$", Pattern.CASE_INSENSITIVE);

    public static @NotNull Tag resolveTag(@NotNull String value) {
        if (value.isEmpty()) {
            return Tag.STR;
        }

        if (BOOL_PATTERN.matcher(value).matches()) {
            return Tag.BOOL;
        }

        if (INT_PATTERN.matcher(value).matches()) {
            return Tag.INT;
        }

        if (FLOAT_PATTERN.matcher(value).matches()) {
            return Tag.FLOAT;
        }

        return Tag.STR;
    }

    // Parses a string as a scalar while retaining all string information so that
    // the resulting Object#toString always matches the input string.
    public static Object parseAsScalar(@NotNull String string) {
        Tag tag = resolveTag(string);
        return parseAsScalar(tag, string);
    }

    public static Object parseAsScalar(@NotNull Tag tag, @NotNull String string) {
        if (tag.equals(Tag.STR)) {
            return string;
        }

        if (tag.equals(Tag.BOOL)) {
            Boolean bool = ParseUtil.parseBooleanOrNull(string);
            return bool != null ? bool : string;
        }

        if (tag.equals(Tag.INT)) {
            Integer integerNumber = ParseUtil.parseIntegerOrNull(string);
            return integerNumber != null ? integerNumber : string;
        }

        if (tag.equals(Tag.FLOAT)) {
            Double doubleNumber = ParseUtil.parseDoubleOrNull(string);
            return doubleNumber != null ? doubleNumber : string;
        }

        return string;
    }

    public static @NotNull List<Object> parseAllAsScalars(String... strings) {
        List<Object> parsedScalars = new ArrayList<>();
        for (String string : strings) {
            parsedScalars.add(parseAsScalar(string));
        }
        return parsedScalars;
    }

    public static @NotNull Tag getTag(@NotNull Object scalar) {
        if (scalar instanceof Boolean) {
            return Tag.BOOL;
        }

        if (scalar instanceof Character || scalar instanceof String) {
            return Tag.STR;
        }

        if (scalar instanceof Byte || scalar instanceof Short || scalar instanceof Integer
                || scalar instanceof Long || scalar instanceof BigInteger) {
            return Tag.INT;
        }

        return Tag.FLOAT;
    }

    public static boolean isBoolean(@NotNull Object object) {
        return object instanceof Boolean;
    }

    public static boolean isCharacter(@NotNull Object object) {
        String string = object.toString();
        return string.length() == 1;
    }

    public static boolean isInteger(@NotNull Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer;
    }

    public static boolean isLong(@NotNull Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer
                || object instanceof Long;
    }

    // checks also Double because snakeyaml parses decimals as double not float
    public static boolean isFloat(@NotNull Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Float || object instanceof Double;
    }

    public static boolean isDouble(@NotNull Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Float || object instanceof Double;
    }

}
