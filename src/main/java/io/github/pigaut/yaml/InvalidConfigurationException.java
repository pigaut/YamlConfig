package io.github.pigaut.yaml;

import io.github.pigaut.yaml.node.line.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.regex.*;

public class InvalidConfigurationException extends ConfigurationException {

    private final ConfigField field;
    private final @Nullable String prefix;
    private final @Nullable String problem;
    private final @Nullable File file;
    private final @Nullable String path;
    private final @Nullable String line;
    private final String cause;
    private final boolean debug;

    public InvalidConfigurationException(ConfigField field, String cause) {
        this(field.getRoot(), field, null, cause);
    }

    public InvalidConfigurationException(ConfigField field, String key, String cause) {
        this(field.getRoot(), field, field.isRoot() ? key : field.getSimplePath() + "." + key, cause);
    }

    public InvalidConfigurationException(ConfigField field, int index, String cause) {
        this(field.getRoot(), field, field.isRoot() ? "[" + (index + 1) + "]" : field.getSimplePath() + "[" + (index + 1) + "]" , cause);
    }

    public InvalidConfigurationException(InvalidConfigurationException exception, String cause) {
        super(null, null, false, exception.debug);
        this.field = exception.field;
        this.prefix = exception.prefix;
        this.problem = exception.problem;
        this.file = exception.file;
        this.path = exception.path;
        this.line = exception.line;
        this.cause = cause;
        this.debug = exception.debug;
    }

    private InvalidConfigurationException(ConfigRoot config, ConfigField field, String path, String cause) {
        super(null, null, false, config.isDebug());
        this.field = field;
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.problem = config.getCurrentProblem();
        this.path = path;
        this.line = field instanceof ConfigLine configLine ? configLine.getValue() :
                field instanceof LineScalar lineScalar ? lineScalar.toLine().getValue() : null;
        this.cause = cause;
        this.debug = config.isDebug();
    }

    public @NotNull ConfigField getField() {
        return field;
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

    public @Nullable String getFilePath() {
        return file != null ? file.getPath() : null;
    }

    public @Nullable String getFilePath(String parentDirectory) {
        if (file != null) {
            return file.getPath().replaceAll(Pattern.quote(parentDirectory + File.separator), "");
        }
        return null;
    }

    public @Nullable String getPath() {
        return path;
    }

    public @Nullable String getLine() {
        return line;
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
        String path = getPath();

        final String optionalPrefix = prefix != null ? (prefix + " ") : "";
        final String optionalProblem = problem != null ? (": &f" + problem.toUpperCase()) : "";
        final String optionalFile = file != null ? ("  &c&lFile &c>> " + file + "\n") : "";
        final String optionalPath = path != null ? ("  &c&lPath &c>> " + path + "\n") : "";
        final String optionalLine = line != null ? ("  &f&lLine &f>> " + line + "\n") : "";
        final String details = "  &e&lDetails &e>> " + cause + "\n";

        return "&c&l" + optionalPrefix + "Configuration" + optionalProblem + "\n" +
                optionalFile +
                optionalPath +
                optionalLine +
                details +
                "&c&l---------------------------------------------";
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
