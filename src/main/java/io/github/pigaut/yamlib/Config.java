package io.github.pigaut.yamlib;

import io.github.pigaut.yamlib.configurator.*;
import io.github.pigaut.yamlib.parser.*;
import org.jetbrains.annotations.*;

import java.io.*;

public interface Config extends ConfigSection {

    /**
     * Retrieves the name of the configuration.
     *
     * @return The name of the configuration, never {@code null}.
     */
    @NotNull String getName();

    /**
     * Retrieves the file associated with the configuration.
     *
     * @return The {@link File} object representing the configuration file, never {@code null}.
     */
    @NotNull File getFile();

    /**
     * Retrieves the configurator associated with this configuration.
     * The configurator loads/maps objects to/from a config section.
     *
     * @return The {@link Configurator} for this configuration, never {@code null}.
     */
    @NotNull Configurator getConfigurator();

    /**
     * Sets the configurator for this configuration.
     * The configurator loads/maps objects to/from a config section.
     *
     * @param configurator The {@link Configurator} to associate with this configuration; cannot be {@code null}.
     */
    void setConfigurator(@NotNull Configurator configurator);

    /**
     * Loads the configuration from the associated file.
     * @return true if config was loaded successfully, false otherwise
     */
    boolean load();

    /**
     * Saves the current state of the configuration to the associated file.
     * @return true if config was saved successfully, false otherwise
     */
    boolean save();

    /**
     * Deletes the configuration file and removes all configuration data.
     */
    void remove();

}
