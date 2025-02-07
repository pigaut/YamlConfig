package io.github.pigaut.yaml.configurator.loader;

import io.github.pigaut.yaml.parser.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class AbstractLoader<T> implements ConfigLoader<T> {

    private final Map<String, ConfigLoader<? extends T>> loadersByName = new HashMap<>();

    public @Nullable ConfigLoader<? extends T> getLoader(String id) {
        return loadersByName.get(StringFormatter.toIdentifier(id));
    }

    public void addLoader(String id, ConfigLoader<? extends T> loader) {
        loadersByName.put(StringFormatter.toIdentifier(id), loader);
    }

    public void removeLoader(String id) {
        loadersByName.remove(StringFormatter.toIdentifier(id));
    }

}
