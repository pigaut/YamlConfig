package io.github.pigaut.yaml;

@FunctionalInterface
public interface Requirement<T> {

    boolean test(T value);

    default String getErrorDetails() {
        return "value did not meet a requirement";
    }

}
