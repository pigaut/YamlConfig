package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.FieldType;
import io.github.pigaut.yaml.node.*;
import org.jetbrains.annotations.*;

public interface ConfigField {

    void clear();
    boolean isRoot();
    @NotNull ConfigBranch getParent() throws UnsupportedOperationException;
    @NotNull ConfigRoot getRoot();
    @NotNull String getKey();
    @NotNull Object getValue();
    @NotNull String getPath();
    @NotNull String getSimplePath();
    @NotNull FieldType getFieldType();

    <T> T getRequired(@NotNull Class<T> classType) throws InvalidConfigException;
    <T> ConfigOptional<T> get(@NotNull Class<T> classType);

    <T> void map(T value);

    ConfigOptional<ConfigScalar> toScalar();
    ConfigOptional<ConfigSection> toSection();
    ConfigOptional<ConfigSequence> toSequence();

}
