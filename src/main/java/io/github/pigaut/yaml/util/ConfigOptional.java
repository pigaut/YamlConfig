package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;

import java.util.*;

public class ConfigOptional {

    private ConfigOptional() {}

    public interface ConfigSupplier<T> {
        T get() throws InvalidConfigurationException;
    }

    public static <T> Optional<T> of(ConfigSupplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (InvalidConfigurationException e) {
            return Optional.empty();
        }
    }

}
