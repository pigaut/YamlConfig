package io.github.pigaut.yaml.configurator;

import io.github.pigaut.yaml.configurator.convert.deserialize.*;
import io.github.pigaut.yaml.configurator.convert.serialize.*;
import io.github.pigaut.yaml.configurator.deserialize.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.configurator.serialize.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Configurator {

    private final Map<Class<?>, ConfigLoader<?>> loadersByType = new HashMap<>();
    private final Map<Class<?>, ConfigMapper<?>> mappersByType = new HashMap<>();

    public <T> void addLoader(Class<T> type, ConfigLoader<T> loader) {
        loadersByType.put(type, loader);
    }

    public <T> void addMapper(Class<T> type, ConfigMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

    public <T> void addDeserializer(Class<T> type, Deserializer<T> deserializer) {
        loadersByType.put(type, deserializer);
    }

    public <T> void addSerializer(Class<T> type, Serializer<T> serializer) {
        mappersByType.put(type, serializer);
    }

    public <T> @Nullable ConfigLoader<T> getLoader(@NotNull Class<T> classType) {
        @SuppressWarnings("unchecked")
        final ConfigLoader<T> loader = (ConfigLoader<T>) loadersByType.get(classType);
        if (loader != null) {
            return loader;
        }
        if (Enum.class.isAssignableFrom(classType)) {
            return Deserializers.enumDeserializer((Class) classType);
        }
        return null;
    }

    public <T> ConfigMapper<? super T> getMapper(Class<T> type) {
        @SuppressWarnings("unchecked")
        ConfigMapper<T> mapper = (ConfigMapper<T>) mappersByType.get(type);

        if (mapper != null) {
            return mapper;
        }

        if (Enum.class.isAssignableFrom(type)) {
            return Serializers.defaultSerializer();
        }

        for (Class<?> mapperType : mappersByType.keySet()) {
            if (mapperType.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                final ConfigMapper<? super T> parentMapper = (ConfigMapper<? super T>) mappersByType.get(mapperType);
                return parentMapper;
            }
        }
        return null;
    }

}
