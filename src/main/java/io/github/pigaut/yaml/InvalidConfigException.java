package io.github.pigaut.yaml;

import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.scalar.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class InvalidConfigException extends ConfigException {

    private final ConfigField field;
    private final @Nullable String prefix;
    private @Nullable String error;
    private final @Nullable File file;
    private final @Nullable String path;
    private final @Nullable String topLevelKey;
    private final @Nullable String line;
    private final String details;

    public InvalidConfigException(@NotNull ConfigField field, String details) {
        this(field.getRoot(), field, field.isRoot() ? null : field.getKey(), details);
    }

    public InvalidConfigException(@NotNull ConfigField field, @NotNull String key, String details) {
        this(field.getRoot(), field, key, details);
    }

    public InvalidConfigException(@NotNull ConfigField field, int index, String details) {
        this(field.getRoot(), field, index, details);
    }

    public InvalidConfigException(@NotNull InvalidConfigException exception, @Nullable String details) {
        this.field = exception.field;
        this.prefix = exception.prefix;
        this.error = exception.error;
        this.file = exception.file;
        this.path = exception.path;
        this.topLevelKey = exception.topLevelKey;
        this.line = exception.line;
        this.details = details;
    }

    private InvalidConfigException(@NotNull ConfigRoot root, @NotNull ConfigField field, @Nullable Object key, @Nullable String details) {
        this.field = field;
        this.prefix = root.getPrefix();
        this.file = root.getFile();

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
        ConfigField parentField = field;
        while (!parentField.isRoot()) {
            parentField = parentField.getParent();
        }
        topLevelKey = parentField instanceof KeyedField keyedField ? keyedField.getKey() : null;

        // Line
        if (field instanceof KeyedScalar keyedScalar) {
            String value = keyedScalar.toString();
            if (value.length() > 25) {
                value = value.substring(0, 25) + "...";
            }
            line = keyedScalar.getKey() + ": " + value;
        }
        else if (field instanceof LineScalar lineScalar) {
            ConfigLine line = lineScalar.toLine();
            String value = line.getValue();
            if (value.length() > 25) {
                value = value.substring(0, 25) + "...";
            }
            this.line = line.isRoot() ? value : line.getKey() + ": " + value;
        }
        else if (field instanceof ConfigSection section) {
            if (section.isRoot()) {
                line = "{ ... }";
            }
            else if (key != null) {
                String value = section.getString(key.toString()).orElse("...");
                if (value.length() > 25) {
                    value = value.substring(0, 25) + "...";
                }
                line = key + ": " + value;
            }
            else {
                line = section.getKey() + ": { ... }";
            }
        }
        else if (field instanceof KeylessField keylessField) {
            ConfigField parent = keylessField.getParent();
            if (parent instanceof ConfigSequence parentSequence) {
                if (key instanceof Integer index && index < parentSequence.size()) {
                    StringBuilder lineBuilder = new StringBuilder();
                    if (!parentSequence.isRoot()) {
                        lineBuilder.append(parentSequence.getKey());
                    }

                    lineBuilder.append("[").append(index + 1).append("]: ");

                    String value = parentSequence.getString(index).orElse("...");
                    if (value.length() > 25) {
                        value = value.substring(0, 25) + "...";
                    }
                    lineBuilder.append(value);

                    line = lineBuilder.toString();
                }
                else {
                    line = parentSequence.getKey() + ": [ ... ]";
                }
            } else {
                line = null;
            }
        }
        else if (field instanceof ConfigScalar configScalar) {
            line = configScalar.toString();
        }
        else {
            line = null;
        }

        // Details
        if (field instanceof ConfigLine configLine && configLine.getFormat() != null) {
            this.details = "Expected format: " + configLine.getFormat();
        } else {
            this.details = details;
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

    public @Nullable String getTopLevelKey() {
        return topLevelKey;
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
