package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;

import java.io.*;
import java.util.regex.*;

public class ConfigurationLoadException extends ConfigurationException {

    private final @Nullable String prefix;
    private final @Nullable File file;
    private final @NotNull String cause;

    public ConfigurationLoadException(ConfigRoot config, @NotNull String cause) {
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

    public @Nullable String getPrefix() {
        return prefix;
    }

    public @Nullable File getFile() {
        return file;
    }

    public @Nullable String getFilePath() {
        return file != null ? file.getPath() : null;
    }

    public @Nullable String getFilePath(String parentDirectory) {
        if (file != null) {
            return file.getPath().replaceAll(Pattern.quote(parentDirectory + File.separator), "");
        }
        return null;
    }

    public @NotNull String getDetails() {
        return cause;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public @NotNull String getLogMessage(String parentDirectory) {
        final String optionalPrefix = prefix != null ? (prefix + " ") : "";
        final String optionalFile = file != null ? ("File: " + file.getPath().replaceAll(Pattern.quote(parentDirectory), "") + "\n") : "";
        final String details = "Details: " + cause + "\n";
        final String logMessage = optionalPrefix + "Configuration: Invalid yaml format\n" +
                optionalFile +
                details +
                "---------------------------------------------";
        return logMessage;
    }

    @Override
    public String toString() {
        final String optionalPrefix = prefix != null ? (prefix + " ") : "";
        final String optionalFile = file != null ? (" File: " + file.getPath() + "\n") : "";
        final String errorMessage = "%sYaml Parsing Error -> Unable to load yaml file\n" +
                "%s" +
                " Details: %s";
        return String.format(errorMessage, optionalPrefix, optionalFile, cause);
    }

}
