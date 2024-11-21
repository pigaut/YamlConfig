package io.github.pigaut.yamlib.configurator;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.configurator.field.*;
import io.github.pigaut.yamlib.configurator.section.*;
import io.github.pigaut.yamlib.parser.deserializer.*;
import io.github.pigaut.yamlib.parser.serializer.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class Configurator {

    private final Map<Class<?>, ConfigGetter<?>> gettersByType = new LinkedHashMap<>();
    private final Map<Class<?>, ConfigSetter<?>> settersByType = new LinkedHashMap<>();

    private final Map<Class<?>, ConfigLoader<?>> loadersByType = new LinkedHashMap<>();
    private final Map<Class<?>, ConfigMapper<?>> mappersByType = new LinkedHashMap<>();

    public <T> T get(@NotNull Class<T> type, @NotNull ConfigSection section, @NotNull String key) throws InvalidConfigurationException {
        ConfigGetter<? extends T> getter = getConfigGetter(type, section, key);
        if (getter == null) {
            throw new IllegalArgumentException("No config getter found for class: " + type.getSimpleName());
        }
        return getter.get(section, key);
    }

    public <T> void set(@NotNull ConfigSection section, @NotNull String path, @NotNull T value) {
        @SuppressWarnings("unchecked")
        ConfigSetter<? super T> setter = (ConfigSetter<? super T>) getConfigSetter(value.getClass());
        if (setter == null) {
            throw new IllegalArgumentException("No config setter found for class: " + value.getClass().getSimpleName());
        }

        Object generatedValue = setter.generateValue(value);
        section.set(path, generatedValue);
    }

    public <T> T load(@NotNull Class<T> type, @NotNull ConfigSection section) throws InvalidConfigurationException {
        ConfigLoader<? extends T> loader = getConfigLoader(type, section);
        if (loader == null) {
            throw new IllegalArgumentException("No section loader found for class: " + type.getSimpleName());
        }
        return loader.load(section);
    }

    public <T> void map(@NotNull ConfigSection section, @NotNull T value) {
        @SuppressWarnings("unchecked")
        ConfigMapper<? super T> mapper = (ConfigMapper<? super T>) getConfigMapper(value.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class: " + value.getClass().getSimpleName());
        }
        mapper.map(section, value);
    }

    public <T> @Nullable ConfigGetter<? extends T> getConfigGetter(@NotNull Class<T> type, @NotNull ConfigSection section, @NotNull String key) {
        if (Enum.class.isAssignableFrom(type)) {
            return (ConfigDeserializer) Deserializers.enumDeserializer((Class) type)::deserialize;
        }

        @SuppressWarnings("unchecked")
        ConfigGetter<T> getter = (ConfigGetter<T>) gettersByType.get(type);
        if (getter != null) {
            return getter;
        }

        int incompatibleKeys = 0;
        for (Class<?> getterType : gettersByType.keySet()) {
            if (type.isAssignableFrom(getterType)) {
                @SuppressWarnings("unchecked")
                ConfigGetter<? extends T> childGetter = (ConfigGetter<? extends T>) gettersByType.get(getterType);
                if (childGetter.matchKey(key)) {
                    return childGetter;
                }
                incompatibleKeys++;
            }
        }

        if (incompatibleKeys != 0) {
            throw new InvalidConfigurationException(section, key, " does not match any " + type.getSimpleName());
        }

        return null;
    }

    public <T> ConfigSetter<? super T> getConfigSetter(Class<T> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return (ConfigSerializer) Serializers.defaultSerializer()::serialize;
        }

        @SuppressWarnings("unchecked")
        ConfigSetter<T> setter = (ConfigSetter<T>) settersByType.get(type);

        if (setter != null) {
            return setter;
        }

        for (Class<?> setterType : settersByType.keySet()) {
            if (setterType.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ConfigSetter<? super T> parentSetter = (ConfigSetter<? super T>) settersByType.get(setterType);
                return parentSetter;
            }
        }

        return null;
    }

    public <T> @Nullable ConfigLoader<? extends T> getConfigLoader(@NotNull Class<T> type, @NotNull ConfigSection section) {
        @SuppressWarnings("unchecked")
        ConfigLoader<T> loader = (ConfigLoader<T>) loadersByType.get(type);
        if (loader != null) {
            return loader;
        }

        int incompatibleSchemas = 0;
        for (Class<?> loaderType : loadersByType.keySet()) {
            if (type.isAssignableFrom(loaderType)) {
                @SuppressWarnings("unchecked")
                ConfigLoader<? extends T> childLoader = (ConfigLoader<? extends T>) loadersByType.get(loaderType);
                if (childLoader.matchSchema(section)) {
                    return childLoader;
                }
                incompatibleSchemas++;
            }
        }
        if (incompatibleSchemas != 0) {
            throw new InvalidConfigurationException(section, " does not match any " + type.getSimpleName());
        }

        return null;
    }

    public <T> ConfigMapper<? super T> getConfigMapper(Class<T> type) {
        @SuppressWarnings("unchecked")
        ConfigMapper<T> mapper = (ConfigMapper<T>) mappersByType.get(type);

        if (mapper != null) {
            return mapper;
        }

        for (Class<?> mapperType : mappersByType.keySet()) {
            if (mapperType.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                ConfigMapper<? super T> parentMapper = (ConfigMapper<? super T>) mappersByType.get(mapperType);
                return parentMapper;
            }
        }

        return null;
    }

    public <T> void addGetter(Class<T> type, ConfigGetter<T> getter) {
        gettersByType.put(type, getter);
    }

    public <T> void addGetter(Class<T> type, String id, ConfigGetter<T> getter) {
        gettersByType.put(type, new ConfigGetter<T>() {
            @Override
            public boolean matchKey(String key) {
                return id.equals(StringFormatter.toConstantCase(key));
            }

            @Override
            public @NotNull T get(@NotNull ConfigSection section, @NotNull String key) {
                return getter.get(section, key);
            }
        });
    }

    public <T, U> void addGetter(Class<T> type, ConfigGetter<U> getter, Function<U, T> factory) {
        gettersByType.put(type, (section, key) -> factory.apply(getter.get(section, key)));
    }

    public <T, U> void addGetter(Class<T> type, String id, ConfigGetter<U> getter, Function<U, T> factory) {
        gettersByType.put(type, new ConfigGetter<T>() {
            @Override
            public boolean matchKey(String key) {
                return id.equals(StringFormatter.toConstantCase(key));
            }

            @Override
            public @NotNull T get(@NotNull ConfigSection section, @NotNull String key) {
                return factory.apply(getter.get(section, key));
            }
        });
    }

    public <T> void addDeserializer(Class<T> type, ConfigDeserializer<T> deserializer) {
        gettersByType.put(type, deserializer);
    }

    public <T> void addSetter(Class<T> type, ConfigSetter<T> setter) {
        settersByType.put(type, setter);
    }

    public <T> void addSerializer(Class<T> type, ConfigSerializer<T> serializer) {
        settersByType.put(type, serializer);
    }

    public <T> void addLoader(Class<T> type, ConfigLoader<T> loader) {
        loadersByType.put(type, loader);
    }

    public <T> void addLoader(Class<T> type, String id, ConfigLoader<T> loader) {
        loadersByType.put(type, new ConfigLoader<T>() {
            @Override
            public boolean matchSchema(ConfigSection section) {
                return section.getString("type", StringFormatter.CONSTANT).equals(id);
            }

            @Override
            public @NotNull T load(@NotNull ConfigSection section) throws InvalidConfigurationException {
                return loader.load(section);
            }
        });
    }

    public <T> void addMapper(Class<T> type, ConfigMapper<T> mapper) {
        mappersByType.put(type, mapper);
    }

}
