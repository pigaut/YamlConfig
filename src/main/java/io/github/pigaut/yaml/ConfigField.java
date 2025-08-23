package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.FieldType;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

public interface ConfigField {

    @NotNull String getKey();

    @NotNull Object getValue();

    boolean isRoot();

    @NotNull
    FieldType getFieldType();

    @NotNull
    ConfigRoot getRoot();

    @NotNull ConfigField getParent();

    @Nullable String getPath();

    @NotNull String getPath(String key);

    void clear();

    <T> ConfigOptional<T> load(Class<T> classType);

    ConfigOptional<ConfigScalar> toScalar();

    ConfigOptional<ConfigSection> toSection();

    ConfigOptional<ConfigSequence> toSequence();

    ConfigOptional<ConfigBranch> toBranch();

}
