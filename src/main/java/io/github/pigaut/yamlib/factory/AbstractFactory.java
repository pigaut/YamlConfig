package io.github.pigaut.yamlib.factory;

import org.jetbrains.annotations.*;

import java.util.*;

public class AbstractFactory<T> {

    private final Map<String, Factory<T>> factoriesById = new HashMap<>();

    public @Nullable Factory<T> getFactory(String id) {
        return factoriesById.get(id);
    }

    public void addFactory(String id, Factory<T> factory) {
        factoriesById.put(id, factory);
    }

    public void removeFactory(String id) {
        factoriesById.remove(id);
    }

    public void clearFactories() {
        factoriesById.clear();
    }

}
