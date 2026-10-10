package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public interface FieldKey {

    @Nullable
    ConfigField getField(@NotNull Branch branch);
    void removeField(@NotNull Branch branch);

    @NotNull Scalar createScalar(@NotNull Branch branch, @NotNull Object value, boolean attached);
    @NotNull Section createSection(@NotNull Branch branch, boolean attached);
    @NotNull Sequence createSequence(@NotNull Branch branch, boolean attached);

}