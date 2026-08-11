package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.scalar.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.scalar.key.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class InvalidConfigException extends ConfigException {

    private final ConfigField field;
    private final @Nullable String prefix;
    private final @Nullable File file;
    private final @Nullable String fileName;
    private final @Nullable Object key;

    private final @Nullable String path;
    private final @Nullable String topLevelKey;
    private final @Nullable String line;
    private final @Nullable String details;

    private @Nullable String error;

    public InvalidConfigException(@NotNull ConfigField field, @Nullable String details) {
        this(field.getRoot(), field, null, details);
    }

    public InvalidConfigException(@NotNull ConfigField field, @NotNull String key, @Nullable String details) {
        this(field.getRoot(), field, key, details);
    }

    public InvalidConfigException(@NotNull ConfigField field, int index, @Nullable String details) {
        this(field.getRoot(), field, index, details);
    }

    public InvalidConfigException(@NotNull InvalidConfigException exception, @Nullable String details) {
        this.field = exception.field;
        this.prefix = exception.prefix;
        this.file = exception.file;
        this.fileName = exception.fileName;
        this.key = exception.key;

        this.path = exception.path;
        this.topLevelKey = exception.topLevelKey;
        this.line = exception.line;
        this.details = details;

        this.error = exception.error;
    }

    private InvalidConfigException(@NotNull ConfigRoot root, @NotNull ConfigField field, @Nullable Object key, @Nullable String details) {
        // key to field
        if (field instanceof ScalarKey scalarKey) {
            field = scalarKey.getField();
        }

        // in-line scalar to complete line
        if (field instanceof LineScalar lineScalar) {
            field = lineScalar.toLine();
        }

        // Line to scalar
        if (field instanceof ConfigLine lineField) {
            field = lineField.toScalar();
        }

        if (!(field instanceof ConfigBranch)) {
            key = null;
        }

        this.field = field;
        this.prefix = root.getPrefix();
        this.file = root.getFile();
        this.fileName = root.getName();
        this.key = key;

        // Path
        if (field instanceof ConfigSection section && key != null) {
            path = section.isRoot() ? key.toString() : section.getPath() + "." + key;
        }
        else if (field instanceof ConfigSequence sequence && key instanceof Integer index) {
            path = sequence.isRoot() ? "[" + index + "]" : sequence.getPath() + "[" + index + "]";
        }
        else if (key == null) {
            path = !field.isRoot() ? field.getPath() : null;
        }
        else {
            path = key.toString();
        }

        // Top level key
        if (field.isRoot()) {
            topLevelKey = key != null ? key.toString() : null;
        }
        else {
            ConfigField topLevelField = field;
            while (!topLevelField.getParent().isRoot()) {
                topLevelField = topLevelField.getParent();
            }
            topLevelKey = topLevelField instanceof KeyedField keyedField ? keyedField.getKey() : null;
        }

        // Line
        line = ConfigFieldDescriber.describe(field, key);

        // Details
        if (field instanceof ConfigLine configLine && configLine.getFormat() != null) {
            this.details = "Expected format: " + configLine.getFormat();
        } else {
            this.details = Objects.requireNonNullElse(details, "Could not process the provided configuration");
        }
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

    public void setErrorIfMissing(@Nullable String error) {
        if (this.error == null) {
            this.error = error;
        }
    }

    public @Nullable File getFile() {
        return file;
    }

    public @Nullable Object getKey() {
        return key;
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

    public @Nullable String getTopLevelKey() {
        return topLevelKey;
    }

    public @Nullable String getFileName() {
        return fileName;
    }

    public @Nullable String getLine() {
        return line;
    }

    public @Nullable String getDetails() {
        return details;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        return "InvalidConfigException{" +
                "field=" + field +
                ", \nprefix='" + prefix + '\'' +
                ", \nerror='" + error + '\'' +
                ", \nfile=" + file +
                ", \nkey=" + key +
                ", \npath='" + path + '\'' +
                ", \ntopLevelKey='" + topLevelKey + '\'' +
                ", \nline='" + line + '\'' +
                ", \ndetails='" + details + '\'' +
                "\n}";
    }

}
