package io.github.pigaut.yaml;

import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.regex.*;

public class InvalidConfigurationException extends ConfigurationException {

    protected final @Nullable String prefix;
    protected final @Nullable String problem;
    protected final @Nullable File file;
    protected final @Nullable String path;
    protected final @Nullable String line;
    protected final @NotNull String cause;
    protected final boolean debug;

    public InvalidConfigurationException(@NotNull InvalidConfigurationException exception, @NotNull String cause) {
        super(null, null, false, exception.debug);
        this.prefix = exception.prefix;
        this.problem = exception.problem;
        this.file = exception.file;
        this.path = exception.path;
        this.line = null;
        this.cause = cause;
        this.debug = exception.debug;
    }

    public InvalidConfigurationException(ConfigLine line, String cause) {
        this(line.getRoot(), line.getRoot().getCurrentProblem(), line.getPath(), line.getValue(), cause);
    }

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
        this.line = null;
        this.cause = cause;
        this.debug = config.isDebug();
    }

    public InvalidConfigurationException(@NotNull ConfigRoot config, @Nullable String problem, @Nullable String path,
                                         @Nullable String line, @NotNull String cause) {
        super(null, null, false, config.isDebug());
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.problem = problem;
        this.path = path;
        this.line = line;
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
