package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.line.scalar.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.regex.*;

public class InvalidConfigException extends ConfigException {

    private final ConfigField field;
    private final @Nullable String prefix;
    private @Nullable String error;
    private final @Nullable File file;
    private final @Nullable String path;
    private final @Nullable String line;
    private final String details;

    public InvalidConfigException(ConfigField field, String cause) {
        this(field.getRoot(), field, field.isRoot() ? null : field.getSimplePath(), cause);
    }

    public InvalidConfigException(ConfigField field, String key, String cause) {
        this(field.getRoot(), field, field.isRoot() ? key : field.getSimplePath() + "." + key, cause);
    }

    public InvalidConfigException(ConfigField field, int index, String cause) {
        this(field.getRoot(), field, field.isRoot() ? "[" + (index + 1) + "]" : field.getSimplePath() + "[" + (index + 1) + "]" , cause);
    }

    public InvalidConfigException(InvalidConfigException exception, String details) {
        this.field = exception.field;
        this.prefix = exception.prefix;
        this.error = exception.error;
        this.file = exception.file;
        this.path = exception.path;
        this.line = exception.line;
        this.details = details;
    }

    private InvalidConfigException(ConfigRoot config, ConfigField field, @Nullable String path, String details) {
        this.field = field;
        this.prefix = config.getPrefix();
        this.file = config.getFile();
        this.path = path;
        this.line = field instanceof ConfigLine configLine ? configLine.getValue() :
                field instanceof LineScalar lineScalar ? lineScalar.toLine().getValue() : null;
        this.details = details;
    }

    public @NotNull ConfigField getField() {
        return field;
    }

    public @Nullable String getPrefix() {
        return prefix;
    }

    public @Nullable String getError() {
        return error;
    }

    public void setError(@Nullable String error) {
        this.error = error;
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
        return details;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        String optionalPrefix = prefix != null ? (prefix + " ") : "";
        String optionalError = error != null ? (": " + CaseFormatter.toSpacedUpperCase(error)) : "";
        String optionalFile = file != null ? (" File >> " + file.getPath() + "\n") : "";
        String optionalPath = path != null ? (" Path >> " + path + "\n") : "";
        String optionalLine = line != null ? (" Line >> " + line + "\n") : "";

        String errorMessage = "%sConfiguration Error%s\n" +
                "%s" +
                "%s" +
                "%s" +
                " Details >> %s.\n\n";
        return String.format(errorMessage, optionalPrefix, optionalError, optionalFile,
                optionalPath, optionalLine, details);
    }

}
