package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.function.*;

public interface ConfigRoot extends ConfigField {

    @Nullable String getPrefix();

    void setPrefix(@Nullable String prefix);

    @Nullable String getCurrentProblem();

    void addProblem(@Nullable String problemDescription);

    void removeProblem(@Nullable String problemDescription);

    @NotNull
    Configurator getConfigurator();

    void setConfigurator(@NotNull Configurator configurator);

    @Nullable File getFile();

    @Nullable String getName();

    @NotNull String getHeader();

    void setHeader(@NotNull String... lines);

    void load() throws ConfigLoadException;

    void load(@NotNull Consumer<ConfigLoadException> errorCollector);

    void load(@NotNull File file) throws ConfigLoadException;

    void load(@NotNull InputStream inputStream) throws ConfigLoadException;

    void load(@NotNull Reader reader) throws ConfigLoadException;

    boolean save();

    boolean save(@NotNull File file);

    String saveToString();

}
