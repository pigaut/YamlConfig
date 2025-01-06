package io.github.pigaut.yaml.configurator.mapper;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface SequenceMapper<T> extends ConfigMapper<T> {

    void mapSequence(@NotNull ConfigSequence sequence, @NotNull T value);

    @Override
    default @NotNull MappingType getDefaultMappingType() {
        return MappingType.SEQUENCE;
    }

}
