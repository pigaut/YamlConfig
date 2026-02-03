package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;

import java.util.*;

public class Requirements {

    public static <T> Requirement<T> isInstance(Class<T> classType) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                return classType.isInstance(value);
            }

            @Override
            public String getErrorDetails() {
                return "value must be of type " + CaseFormatter.toTitleCase(classType);
            }
        };
    }

    public static <T extends Collection<?>> Requirement<T> sizeRange(int min, int max) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                return value.size() >= min && value.size() <= max;
            }

            @Override
            public String getErrorDetails() {
                return "list size must be greater than " + min + " and smaller than " + max;
            }
        };
    }

    public static <T extends Collection<?>> Requirement<T> minSize(int min) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                return value.size() >= min;
            }

            @Override
            public String getErrorDetails() {
                return "list size must be greater than " + min;
            }
        };
    }

    public static <T extends Collection<?>> Requirement<T> maxSize(int max) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                return value.size() <= max;
            }

            @Override
            public String getErrorDetails() {
                return "list size must be smaller than " + max;
            }
        };
    }

    public static <T extends Number> Requirement<T> greaterThan(T min) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                return value.doubleValue() > min.doubleValue();
            }

            @Override
            public String getErrorDetails() {
                return "value must be greater than " + min;
            }
        };
    }

    public static <T extends Number> Requirement<T> isPositive() {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                return value.doubleValue() > 0;
            }

            @Override
            public String getErrorDetails() {
                return "value must be greater than 0";
            }
        };
    }

    public static <T extends Number> Requirement<T> smallerThan(T max) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                return value.doubleValue() < max.doubleValue();
            }

            @Override
            public String getErrorDetails() {
                return "value must be less than " + max;
            }
        };
    }

    public static <T extends Number> Requirement<T> inRange(T min, T max) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                double doubleNumber = value.doubleValue();
                return doubleNumber >= min.doubleValue() && doubleNumber <= max.doubleValue();
            }

            @Override
            public String getErrorDetails() {
                return "value must be greater than " + min + " and less than " + max;
            }
        };
    }

}
