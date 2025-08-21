package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.FieldType;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface SequenceMapper<T> extends ConfigMapper<T> {

    void mapSequence(@NotNull ConfigSequence sequence, @NotNull T value);

    @Override
    default @NotNull FieldType getDefaultMappingType() {
        return FieldType.SEQUENCE;
    }

}
