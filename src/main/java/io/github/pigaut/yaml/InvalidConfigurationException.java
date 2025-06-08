package io.github.pigaut.yaml;

import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.regex.*;

public class InvalidConfigurationException extends ConfigurationException {

    private final @Nullable String prefix;
    private final @Nullable String problem;
    private final @Nullable File file;
    private final @Nullable String path;
    private final @NotNull String cause;
    private final boolean debug;

    public InvalidConfigurationException(ConfigField field, String cause) {
        this(field.getRoot(), field.getRoot().getCurrentProblem(), field.getPath(), cause);
    }

    public InvalidConfigurationException(ConfigField field, String key, String cause) {
        this(field.getRoot(), field.getRoot().getCurrentProblem(), field.getPath(key), cause);
    }

    public InvalidConfigurationException(ConfigField field, String key, String problem, String cause) {
        this(field.getRoot(), problem, field.getPath(key), cause);
    }

    public InvalidConfigurationException(ConfigField field, int index, String cause) {
        this(field.getRoot(), field.getRoot().getCurrentProblem(), field.getPath() + "[" + index + "]", cause);
    }

    public InvalidConfigurationException(@NotNull ConfigRoot config, @Nullable String problem, @Nullable String path, @NotNull String cause) {
        super(null, null, false, config.isDebug());
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.problem = problem;
        this.path = path;
        this.cause = cause;
        this.debug = config.isDebug();
    }

    public @Nullable String getPrefix() {
        return prefix;
    }

    public @Nullable String getProblem() {
        return problem;
    }

    public @Nullable File getFile() {
        return file;
    }

    public @Nullable String getPath() {
        return path;
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
        final String optionalProblem = problem != null ? (": " + problem) : "";
        final String optionalFile = file != null ? ("File: " + file.getPath().replaceAll(Pattern.quote(parentDirectory), "") + "\n") : "";
        final String optionalPath = path != null ? ("Path: " + path + "\n") : "";
        final String details = "Details: " + cause + "\n";
        final String logMessage = optionalPrefix + "Configuration" + optionalProblem + "\n" +
                optionalFile +
                optionalPath +
                details +
                "---------------------------------------------";
        return logMessage;
    }

    @Override
    public String toString() {
        final String optionalPrefix = prefix != null ? (prefix + " ") : "";
        final String optionalProblem = problem != null ? (" -> " + problem) : "";
        final String optionalFile = file != null ? (" File: " + file.getPath() + "\n") : "";
        final String optionalPath = path != null ? (" Path: " + path + "\n") : "";

        final String errorMessage = "%sConfiguration Error%s\n" +
                "%s" +
                "%s" +
                " Details: %s." + (debug ? "\n\n" : "");
        return String.format(errorMessage, optionalPrefix, optionalProblem, optionalFile, optionalPath, cause);
    }

}
