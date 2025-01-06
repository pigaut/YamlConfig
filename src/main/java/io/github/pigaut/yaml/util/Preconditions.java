package io.github.pigaut.yaml.util;

public class Preconditions {

    private Preconditions() {}

    public static void checkNotNull(Object reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkOperation(boolean expression, String errorMessage) {
        if (!expression) {
            throw new UnsupportedOperationException(errorMessage);
        }
    }

    public static void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
