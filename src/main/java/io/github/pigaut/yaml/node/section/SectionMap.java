package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

@ApiStatus.Internal
public interface SectionMap {

    @Nullable KeyedField getNode(@NotNull String key);

    void addNode(@NotNull KeyedField keyedField);

    void removeNode(@NotNull String key);

    void updateKey(@NotNull KeyedField field, @NotNull String oldKey, @NotNull String newKey);

    @NotNull Scalar createScalar(@NotNull String path, @NotNull Object value, boolean attached);
    @NotNull Section createSection(@NotNull String path, boolean attached);
    @NotNull Sequence createSequence(@NotNull String path, boolean attached);

    @NotNull Scalar createScalarByKey(@NotNull String key, @NotNull Object value);
    @NotNull Section createSectionByKey(@NotNull String key);
    @NotNull Sequence createSequenceByKey(@NotNull String key);

}
