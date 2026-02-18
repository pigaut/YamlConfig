package io.github.pigaut.yaml;

import io.github.pigaut.yaml.amount.*;
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

    public static Requirement<String> minLength(int min) {
        return new Requirement<String>() {
            @Override
            public boolean test(String value) {
                return value.length() > min;
            }

            @Override
            public String getErrorDetails() {
                return "string must contain at least " + min + " characters";
            }
        };
    }

    public static Requirement<String> maxLength(int min) {
        return new Requirement<String>() {
            @Override
            public boolean test(String value) {
                return value.length() > min;
            }

            @Override
            public String getErrorDetails() {
                return "string cannot be larger than " + min + " characters";
            }
        };
    }

    public static Requirement<String> lengthRange(int min, int max) {
        return new Requirement<String>() {
            @Override
            public boolean test(String value) {
                return value.length() > min;
            }

            @Override
            public String getErrorDetails() {
                return "string must contain " + min + "-" + max + " characters";
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
                return "list size must be between " + min + " and " + max + " (inclusive)";
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

    public static Requirement<Amount> minAmount(double min) {
        return new Requirement<Amount>() {
            @Override
            public boolean test(Amount amount) {
                return amount.test(value -> value > min);
            }

            @Override
            public String getErrorDetails() {
                return "amount must be greater than " + min;
            }
        };
    }

    public static Requirement<Amount> maxAmount(double max) {
        return new Requirement<Amount>() {
            @Override
            public boolean test(Amount amount) {
                return amount.test(value -> value < max);
            }

            @Override
            public String getErrorDetails() {
                return "amount must be less than " + max;
            }
        };
    }
    
    public static Requirement<Amount> positiveAmount() {
        return new Requirement<Amount>() {
            @Override
            public boolean test(Amount amount) {
                return amount.test(value -> value > 0);
            }

            @Override
            public String getErrorDetails() {
                return "amount must be greater than 0";
            }
        };
    }

    public static Requirement<Amount> amountBetween(double min, double max) {
        return new Requirement<>() {
            @Override
            public boolean test(Amount amount) {
                return amount.test(value -> value >= min && value <= max);
            }

            @Override
            public String getErrorDetails() {
                return "amount must be between " + min + " and " + max + " (inclusive)";
            }
        };
    }

    public static <T extends Number> Requirement<T> positive() {
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

    public static <T extends Number> Requirement<T> min(T min) {
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

    public static <T extends Number> Requirement<T> max(T max) {
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

    public static <T extends Number> Requirement<T> between(T min, T max) {
        return new Requirement<>() {
            @Override
            public boolean test(T value) {
                double doubleNumber = value.doubleValue();
                return doubleNumber >= min.doubleValue() && doubleNumber <= max.doubleValue();
            }

            @Override
            public String getErrorDetails() {
                return "value must be between " + min + " and " + max + " (inclusive)";
            }
        };
    }

}
