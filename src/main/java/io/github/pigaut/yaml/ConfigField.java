package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.FieldType;
import org.jetbrains.annotations.*;

import java.util.*;

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

    @Nullable String getProblemDescription();

    void setProblemDescription(@Nullable String problem);

    void clear();

    <T> T load(Class<T> type) throws InvalidConfigurationException;

    <T> Optional<T> loadOptional(Class<T> type);

    @NotNull ConfigScalar toScalar() throws InvalidConfigurationException;
    @NotNull ConfigSection toSection() throws InvalidConfigurationException;
    @NotNull ConfigSequence toSequence() throws InvalidConfigurationException;

    Optional<ConfigScalar> asScalar();
    Optional<ConfigSection> asSection();
    Optional<ConfigSequence> asSequence();

}
