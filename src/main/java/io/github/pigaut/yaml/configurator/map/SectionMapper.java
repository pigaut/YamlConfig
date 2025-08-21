package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.FieldType;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface SectionMapper<T> extends ConfigMapper<T> {

    void mapSection(@NotNull ConfigSection section, @NotNull T value);

    @Override
    default @NotNull FieldType getDefaultMappingType() {
        return FieldType.SECTION;
    }

}
