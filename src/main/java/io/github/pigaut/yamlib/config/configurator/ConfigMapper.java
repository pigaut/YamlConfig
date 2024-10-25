package io.github.pigaut.yamlib.config.configurator;

import io.github.pigaut.yamlib.*;

import java.util.*;

@FunctionalInterface
public interface ConfigMapper<T> {

    void map(ConfigSection section, T value);

    ConfigMapper<Map> MAP = (section, map) -> {
        section.setKeyless(false);
        section.clear();

        map.forEach((key, value) -> {
            section.set(String.valueOf(key), value);
        });
    };

    ConfigMapper<Iterable> ITERABLE = (section, elements) -> {
        section.setKeyless(true);
        section.clear();

        for (Object element : elements) {
            section.add(element);
        }
    };

}
