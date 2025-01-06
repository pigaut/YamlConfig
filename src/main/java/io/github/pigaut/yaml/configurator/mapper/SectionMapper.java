package io.github.pigaut.yaml.configurator.mapper;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface SectionMapper<T> extends ConfigMapper<T> {

    void mapSection(@NotNull ConfigSection section, @NotNull T value);

    @Override
    default @NotNull MappingType getDefaultMappingType() {
        return MappingType.SECTION;
    }

}
