package io.github.pigaut.yaml.util;

import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.nodes.*;

public class ScalarUtil {

    public static @NotNull Tag getTag(Object object) {
        if (isBoolean(object)) {
            return Tag.BOOL;
        }

        if (isLong(object)) {
            return Tag.INT;
        }

        if (isDouble(object)) {
            return Tag.FLOAT;
        }

        return Tag.STR;
    }

    public static boolean isBoolean(Object object) {
        return object instanceof Boolean;
    }

    public static boolean isCharacter(Object object) {
        if (object == null) {
            return false;
        }
        String string = object.toString();
        return string.length() == 1;
    }

    public static boolean isInteger(Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer;
    }

    public static boolean isLong(Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer
                || object instanceof Long;
    }

    // checks also Double because snakeyaml parses decimals as double not float
    public static boolean isFloat(Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Float || object instanceof Double;
    }

    public static boolean isDouble(Object object) {
        return object instanceof Byte || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Float || object instanceof Double;
    }

}
