package io.github.pigaut.yamlib.config.configurator;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Configurator implements ConfigMapper<Object> {

    private final Map<Class<?>, ConfigMapper<?>> mappersByType = new HashMap<>();
    private final Map<Class<?>, ConfigLoader<?>> loadersByType = new HashMap<>();

    public <T> void registerLoader(Class<T> type, ConfigLoader<T> loader) {
        loadersByType.put(type, loader);
    }

    public <T> void registerMapper(Class<T> type, ConfigMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

    public <T> @Nullable ConfigLoader<?> getLoader(@NotNull Class<T> type, @NotNull ConfigSection section) {
        ConfigLoader<T> exactLoader = (ConfigLoader<T>) loadersByType.get(type);
        if (exactLoader != null) {
            return exactLoader;
        }

        int incompatibleSchemas = 0;
        for (Map.Entry<Class<?>, ConfigLoader<?>> entry : loadersByType.entrySet()) {
            if (type.isAssignableFrom(entry.getKey().getClass())) {
                ConfigLoader<?> childLoader = entry.getValue();
                incompatibleSchemas++;

                if (childLoader.matchSchema(section)) {
                    return childLoader;
                }
            }
        }

        if (incompatibleSchemas != 0) {
            throw new InvalidConfigurationException(section, "is not a valid " + type.getSimpleName());
        }

        return null;
    }

    public ConfigMapper<?> getMapper(Class<?> type) {
        ConfigMapper<?> mapper = mappersByType.get(type);
        if (mapper != null) {
            return mapper;
        }

        return mappersByType.keySet().stream()
                .filter(key -> key.isAssignableFrom(type))
                .findFirst()
                .map(mappersByType::get)
                .orElse(null);
    }

    public <T> T load(@NotNull Class<T> type, @NotNull ConfigSection section) throws IllegalArgumentException, InvalidConfigurationException {
        ConfigLoader<?> loader = getLoader(type, section);

        if (loader == null) {
            throw new IllegalArgumentException("No class loader found for class: " + type.getSimpleName());
        }

        return (T) loader.load(section);
    }

    public <T> Optional<T> loadOptional(@NotNull Class<T> type, @NotNull ConfigSection section) {
        try {
            return Optional.of(load(type, section));
        } catch (InvalidConfigurationException e) {
            return Optional.empty();
        }
    }

    @Override
    public void map(@NotNull ConfigSection section, @NotNull Object value) throws IllegalArgumentException {
        Preconditions.checkNotNull(section, "Section cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        ConfigMapper mapper = getMapper(value.getClass());

        if (mapper == null) {
            throw new IllegalArgumentException("No class mapper found for class: " + value.getClass().getSimpleName());
        }

        mapper.map(section, value);
    }

}
