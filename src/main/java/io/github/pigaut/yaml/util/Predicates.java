package io.github.pigaut.yaml.util;

import java.util.*;
import java.util.function.*;

public class Predicates {

    public static <T> Predicate<T> notNull() {
        return Objects::nonNull;
    }

    public static <T> Predicate<T> notNull(T reference) {
        return t -> reference != null;
    }

    public static <T extends Number> Predicate<T> greaterThan(T min) {
        return number -> number.doubleValue() > min.doubleValue();
    }

    public static <T extends Number> Predicate<T> isPositive() {
        return number -> number.doubleValue() > 0;
    }

    public static <T extends Number> Predicate<T> smallerThan(T max) {
        return number -> number.doubleValue() < max.doubleValue();
    }

    public static <T extends Number> Predicate<T> range(T min, T max) {
        return number -> {
            double doubleNumber = number.doubleValue();
            return doubleNumber >= min.doubleValue() && doubleNumber <= max.doubleValue();
        };
    }

}
