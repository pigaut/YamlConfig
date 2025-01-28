package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class ConfigurationLoadException extends RuntimeException {

    private final @Nullable String prefix;
    private final @Nullable File file;
    private final @NotNull String cause;

    public ConfigurationLoadException(ConfigRoot config, String cause) {
        super(null, null, false, config.isDebug());
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.cause = cause;
    }

    public ConfigurationLoadException(ConfigRoot config, Throwable cause) {
        super(null, null, false, config.isDebug());
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.cause = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        final String optionalPrefix = prefix != null ? (prefix + " ") : "";
        final String optionalFile = file != null ? (" File: " + file.getPath() + "\n") : "";
        final String errorMessage = "%sConfiguration Load Error -> Unable to load yaml data\n" +
                "%s" +
                " Details: %s";
        return String.format(errorMessage, optionalPrefix, optionalFile, cause);
    }

}
