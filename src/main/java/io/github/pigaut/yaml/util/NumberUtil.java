package io.github.pigaut.yaml.util;

import org.jetbrains.annotations.*;

public class NumberUtil {

    public static @NotNull String formatDouble(double value) {
        return value % 1 == 0 ? String.valueOf((long) value) : String.valueOf(value);
    }

}
