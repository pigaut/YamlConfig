package io.github.pigaut.yamlib.config.configurator;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Configurator implements ConfigMapper<Object> {

    private final Map<Class<?>, ConfigMapper<?>> mappersByType = new HashMap<>();
    private final Map<Class<?>, ConfigLoader<?>> loadersByType = new HashMap<>();

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

    public <T> T loadOrThrow(@NotNull Class<T> type, @NotNull ConfigSection section) throws IllegalArgumentException, InvalidConfigurationException {
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(section, "Section cannot be null");
        ConfigLoader<T> loader = getExactLoader(type);

        if (loader == null) {
            throw new IllegalArgumentException("No class loader found for class: " + type.getSimpleName());
        }

        return loader.load(section);
    }

    public <T> Optional<T> load(@NotNull Class<T> type, @NotNull ConfigSection section) {
        try {
            return Optional.of(loadOrThrow(type, section));
        } catch (InvalidConfigurationException e) {
            return Optional.empty();
        }
    }

    public <T> ConfigMapper<T> getExactMapper(Class<T> type) {
        return (ConfigMapper<T>) mappersByType.get(type);
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

    public <T> void registerMapper(Class<T> type, ConfigMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

    public <T> ConfigLoader<T> getExactLoader(Class<T> type) {
        return (ConfigLoader<T>) loadersByType.get(type);
    }

    public <T> void registerLoader(Class<T> type, ConfigLoader<T> loader) {
        loadersByType.put(type, loader);
    }

}
