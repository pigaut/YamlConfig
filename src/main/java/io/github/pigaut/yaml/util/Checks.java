package io.github.pigaut.yaml.util;

import org.jetbrains.annotations.*;

public class Checks {

    public static boolean notNull(@Nullable Object value) {
        return value != null;
    }

    public static boolean isNull(@Nullable Object value) {
        return value == null;
    }

    public static <T> boolean isInstance(@NotNull Class<T> classType, Object object) {
        return classType.isInstance(object);
    }

}
