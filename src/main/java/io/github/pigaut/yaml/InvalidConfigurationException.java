package io.github.pigaut.yaml;

import io.github.pigaut.yaml.node.line.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.regex.*;

public class InvalidConfigurationException extends ConfigurationException {

    private final @Nullable String prefix;
    private final @Nullable String problem;
    private final @Nullable File file;
    private final @Nullable String path;
    private final @Nullable String line;
    private final @NotNull String cause;
    private final boolean debug;

    public InvalidConfigurationException(ConfigField field, String cause) {
        this(field, field.getRoot().getCurrentProblem(), field.getSimplePath(), cause);
    }

    public InvalidConfigurationException(ConfigField field, String key, String cause) {
        this(field, field.getRoot().getCurrentProblem(), field.getSimplePath() + "." + key, cause);
    }

    public InvalidConfigurationException(ConfigField field, int index, String cause) {
        this(field, field.getRoot().getCurrentProblem(), field.getSimplePath() + "[" + (index + 1) + "]", cause);
    }

    public InvalidConfigurationException(@NotNull ConfigField field, @Nullable String problem, @Nullable String path, @NotNull String cause) {
        this(field.getRoot(), problem, path,
                (field instanceof ConfigLine configLine ? configLine.getValue() :
                        field instanceof LineScalar lineScalar ? lineScalar.toLine().getValue() : null), cause);
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

    public InvalidConfigurationException(@NotNull InvalidConfigurationException exception, @NotNull String cause) {
        super(null, null, false, exception.debug);
        this.prefix = exception.prefix;
        this.problem = exception.problem;
        this.file = exception.file;
        this.path = exception.path;
        this.line = exception.line;
        this.cause = cause;
        this.debug = exception.debug;
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
        final String optionalFile = file != null ? ("File: " + file.getPath().replaceAll(Pattern.quote(parentDirectory + File.separator), "") + "\n") : "";
        final String optionalPath = path != null ? ("Path: " + path + "\n") : "";
        final String optionalLine = line != null ? ("Line: " + line + "\n") : "";
        final String details = "Details: " + cause + "\n";
        final String logMessage = optionalPrefix + "Configuration" + optionalProblem + "\n" +
                optionalFile +
                optionalPath +
                optionalLine +
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
        final String optionalLine = line != null ? ("Line: " + line + "\n") : "";

        final String errorMessage = "%sConfiguration Error%s\n" +
                "%s" +
                "%s" +
                "%s" +
                " Details: %s." + (debug ? "\n\n" : "");
        return String.format(errorMessage, optionalPrefix, optionalProblem, optionalFile,
                optionalPath, optionalLine, cause);
    }

}
