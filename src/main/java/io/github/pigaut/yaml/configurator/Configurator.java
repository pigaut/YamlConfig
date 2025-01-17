package io.github.pigaut.yaml.configurator;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.loader.*;
import io.github.pigaut.yaml.configurator.mapper.*;
import io.github.pigaut.yaml.configurator.parser.*;
import io.github.pigaut.yaml.parser.deserializer.*;
import io.github.pigaut.yaml.parser.serializer.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class Configurator {

    private final Map<Class<?>, ConfigLoader<?>> loadersByType = new HashMap<>();
    private final Map<Class<?>, ConfigMapper<?>> mappersByType = new HashMap<>();

    public <T> @Nullable ConfigLoader<T> getLoader(@NotNull Class<T> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return (ConfigDeserializer) new EnumDeserializer<>((Class) type)::deserialize;
        }
        @SuppressWarnings("unchecked")
        ConfigLoader<T> loader = (ConfigLoader<T>) loadersByType.get(type);
        return loader;
    }

    public <T> ConfigMapper<? super T> getMapper(Class<T> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return (ConfigSerializer) Serializers.defaultSerializer()::serialize;
        }

        @SuppressWarnings("unchecked")
        ConfigMapper<T> mapper = (ConfigMapper<T>) mappersByType.get(type);

        if (mapper != null) {
            return mapper;
        }

        for (Class<?> setterType : mappersByType.keySet()) {
            if (setterType.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ConfigMapper<? super T> parentMapper = (ConfigMapper<? super T>) mappersByType.get(setterType);
                return parentMapper;
            }
        }

        return null;
    }

    public <T> void addDeserializer(Class<T> type, ConfigDeserializer<T> deserializer) {
        loadersByType.put(type, deserializer);
    }

    public <T> void addSerializer(Class<T> type, ConfigSerializer<T> serializer) {
        mappersByType.put(type, serializer);
    }

    public <T> void addLoader(Class<T> type, ConfigLoader<T> loader) {
        loadersByType.put(type, loader);
    }

    public <T> void addSectionLoader(Class<T> type, SectionLoader<T> loader) {
        loadersByType.put(type, loader);
    }

    public <T> void addSequenceLoader(Class<T> type, SequenceLoader<T> loader) {
        loadersByType.put(type, loader);
    }

    public <T> void addScalarLoader(Class<T> type, ScalarLoader<T> loader) {
        loadersByType.put(type, loader);
    }

    public <T> void addMapper(Class<T> type, ConfigMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

    public <T> void addScalarMapper(Class<T> type, ScalarMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

    public <T> void addScalarMapper(Class<T> type, Function<T, String> keyCreator, Function<T, Object> scalarCreator) {
        mappersByType.put(type, new ScalarMapper<T>() {
            @Override
            public @NotNull String createKey(@NotNull T value) {
                return keyCreator.apply(value);
            }

            @Override
            public @NotNull Object createScalar(@NotNull T value) {
                return scalarCreator.apply(value);
            }
        });
    }

    public <T> void addSectionMapper(Class<T> type, SectionMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

    public <T> void addSectionMapper(Class<T> type, Function<T, String> keyCreator, BiConsumer<ConfigSection, T> sectionMapper) {
        mappersByType.put(type, new SectionMapper<T>() {
            @Override
            public @NotNull String createKey(@NotNull T value) {
                return keyCreator.apply(value);
            }

            @Override
            public void mapSection(@NotNull ConfigSection section, @NotNull T value) {
                sectionMapper.accept(section, value);
            }
        });
    }

    public <T> void addSequenceMapper(Class<T> type, SequenceMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

    public <T> void addSequenceMapper(Class<T> type, Function<T, String> keyCreator, BiConsumer<ConfigSequence, T> sequenceMapper) {
        mappersByType.put(type, new SequenceMapper<T>() {
            @Override
            public @NotNull String createKey(@NotNull T value) {
                return keyCreator.apply(value);
            }

            @Override
            public void mapSequence(@NotNull ConfigSequence sequence, @NotNull T value) {
                sequenceMapper.accept(sequence, value);
            }
        });
    }

}
