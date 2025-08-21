package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.convert.format.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class AbstractLoader<T> implements ConfigLoader<T> {

    private final Map<String, ConfigLoader<? extends T>> loadersByName = new HashMap<>();

    public @Nullable ConfigLoader<? extends T> getLoader(String id) {
        return loadersByName.get(CaseFormatter.toConstantCase(id));
    }

    public void addLoader(String id, ConfigLoader<? extends T> loader) {
        loadersByName.put(CaseFormatter.toConstantCase(id), loader);
    }

    public void removeLoader(String id) {
        loadersByName.remove(CaseFormatter.toConstantCase(id));
    }

}
