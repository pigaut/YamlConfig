package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;

public class Checks {

    public static boolean notNull(@Nullable Object value) {
        return value != null;
    }

    public static boolean isNull(@Nullable Object value) {
        return value == null;
    }

    public static <T> boolean isInstance(@NotNull Class<T> classType, @NotNull Object object) {
        return classType.isInstance(object);
    }

}
