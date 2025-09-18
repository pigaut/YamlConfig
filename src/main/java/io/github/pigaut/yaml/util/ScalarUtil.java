package io.github.pigaut.yaml.util;

public class ScalarUtil {

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
