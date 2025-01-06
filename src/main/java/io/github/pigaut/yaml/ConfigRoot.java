package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.*;
import org.jetbrains.annotations.*;

import java.io.*;

public interface ConfigRoot extends ConfigField {

    @Nullable String getPrefix();

    void setPrefix(@Nullable String prefix);

    boolean isDebug();

    void setDebug(boolean debug);

    @NotNull
    Configurator getConfigurator();

    void setConfigurator(@NotNull Configurator configurator);

    @NotNull File getFile();

    @NotNull String getName();

    String getHeader();

    void setHeader(@NotNull String... lines);

    boolean load() throws ConfigurationLoadException;

    boolean load(@NotNull File file);

    boolean load(@NotNull InputStream inputStream);

    boolean load(@NotNull Reader reader);

    boolean save();

    boolean save(@NotNull File file);

    String saveToString();

}
