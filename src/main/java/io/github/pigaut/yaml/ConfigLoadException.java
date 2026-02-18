package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;

import java.io.*;
import java.util.regex.*;

public class ConfigLoadException extends ConfigException {

    private final @Nullable String prefix;
    private final @Nullable File file;
    private final @NotNull String details;

    public ConfigLoadException(ConfigRoot config, @NotNull String details) {
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.details = details;
    }

    public ConfigLoadException(ConfigRoot config, Throwable cause) {
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.details = cause.getMessage();
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
        return details;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        String optionalPrefix = prefix != null ? (prefix + " ") : "";
        String optionalFile = file != null ? (" File >> " + file.getPath() + "\n") : "";

        String errorMessage = "%sConfiguration: INVALID YAML FILE\n" +
                "%s" +
                " Details >> %s";
        return String.format(errorMessage, optionalPrefix, optionalFile, details);
    }

}
