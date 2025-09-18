package io.github.pigaut.yaml.configurator;

import io.github.pigaut.yaml.configurator.convert.*;
import io.github.pigaut.yaml.configurator.convert.deserialize.*;
import io.github.pigaut.yaml.configurator.convert.serialize.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class Configurator {

    private final Map<Class<?>, ConfigLoader<?>> loadersByType = new HashMap<>();
    private final Map<Class<?>, ConfigMapper<?>> mappersByType = new HashMap<>();

    public <T> void addLoader(Class<T> classType, ConfigLoader<T> loader) {
        loadersByType.put(classType, loader);
    }

    public <T> void addMapper(Class<T> classType, ConfigMapper<T> mapper) {
        mappersByType.put(classType, mapper);
    }

    public <T> void addDeserializer(Class<T> classType, Deserializer<T> deserializer) {
        loadersByType.put(classType, deserializer);
    }

    public <T> void addSerializer(Class<T> classType, Serializer<T> serializer) {
        mappersByType.put(classType, serializer);
    }

    public <T> void addConverter(Class<T> classType, Converter<T> converter) {
        addDeserializer(classType, converter);
        addSerializer(classType, converter);
    }

    public <T> @Nullable ConfigLoader<T> getLoader(@NotNull Class<T> classType) {
        @SuppressWarnings("unchecked")
        final ConfigLoader<T> loader = (ConfigLoader<T>) loadersByType.get(classType);
        if (loader != null) {
            return loader;
        }
        if (Enum.class.isAssignableFrom(classType)) {
            return Deserializers.enumDeserializer((Class<? extends Enum>) classType);
        }
        return null;
    }

    public <T> ConfigMapper<? super T> getMapper(Class<T> classType) {
        @SuppressWarnings("unchecked")
        ConfigMapper<T> mapper = (ConfigMapper<T>) mappersByType.get(classType);

        if (mapper != null) {
            return mapper;
        }

        if (Enum.class.isAssignableFrom(classType)) {
            return Serializers.defaultSerializer();
        }

        for (Class<?> mapperType : mappersByType.keySet()) {
            if (mapperType.isAssignableFrom(classType)) {
                @SuppressWarnings("unchecked")
                final ConfigMapper<? super T> parentMapper = (ConfigMapper<? super T>) mappersByType.get(mapperType);
                return parentMapper;
            }
        }
        return null;
    }

}
