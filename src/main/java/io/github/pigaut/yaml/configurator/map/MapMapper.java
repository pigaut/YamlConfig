package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class MapMapper implements ConfigMapper.Section<Map> {

    @Override
    public void mapToSection(@NotNull ConfigSection section, @NotNull Map mappings) {
        section.clear();
        mappings.forEach((key, value) -> {
            String keyAsString = String.valueOf(key);
            if (value == null) {
                section.getSectionOrCreate(keyAsString);
                return;
            }
            section.set(keyAsString, value);
        });
    }

}
