package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public interface ConfigRoot extends ConfigField, ErrorCollector {

    @Nullable String getPrefix();
    void setPrefix(@Nullable String prefix);

    @NotNull
    Configurator getConfigurator();
    void setConfigurator(@NotNull Configurator configurator);

    boolean hasFile();
    @Nullable File getFile();

    boolean hasName();
    @Nullable String getName();

    @NotNull String getHeader();
    void setHeader(@NotNull String... lines);

    void load() throws ConfigLoadException;
    void loadOrEmpty();

    void loadFromFile(@NotNull File file) throws ConfigLoadException;
    void loadFromStream(@NotNull InputStream inputStream) throws ConfigLoadException;
    void loadFromReader(@NotNull Reader reader) throws ConfigLoadException;

    boolean save();
    boolean save(@NotNull File file);

    @Nullable ConfigLoader<?> getActiveLoader();
    void setActiveLoader(@Nullable ConfigLoader<?> activeLoader);

    @Nullable String saveToString();

}
