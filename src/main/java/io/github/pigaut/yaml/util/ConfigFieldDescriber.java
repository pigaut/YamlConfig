package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

public final class ConfigFieldDescriber {

    private static final int MAX_VALUE_LENGTH = 35;

    private ConfigFieldDescriber() {
    }

    public static @Nullable String describe(@NotNull ConfigField field) {
        return describe(field, null);
    }

    public static @Nullable String describe(@NotNull ConfigField field, @Nullable Object key) {
        if (field instanceof KeyedScalar keyedScalar) {
            return keyedLine(keyedScalar.getKey(), truncate(keyedScalar.toString()));
        }

        if (field instanceof ConfigSequence sequence) {
            return containerPlaceholder(sequence, "[ ... ]");
        }

        if (field instanceof ConfigSection section) {
            return describeSection(section, key);
        }

        if (field instanceof KeylessField keylessField) {
            return describeKeylessField(keylessField, key);
        }

        if (field instanceof ConfigScalar configScalar) {
            return configScalar.toString();
        }

        return null;
    }

    private static @NotNull String describeSection(@NotNull ConfigSection section, @Nullable Object key) {
        if (key == null) {
            return containerPlaceholder(section, "{ ... }");
        }

        String keyString = key.toString();
        if (!section.isScalar(keyString)) {
            return containerPlaceholder(section, "{ ... }");
        }

        Object value = section.getValue(keyString);
        return value != null
                ? keyedLine(keyString, truncate(value.toString()))
                : containerPlaceholder(section, "{ ... }");
    }

    private static @Nullable String describeKeylessField(@NotNull KeylessField keylessField, @Nullable Object key) {
        ConfigField parent = keylessField.getParent();
        if (!(parent instanceof ConfigSequence parentSequence)) {
            return null;
        }

        if (!(key instanceof Integer index) || !parentSequence.isScalar(index)) {
            return containerPlaceholder(keylessField, "[ ... ]");
        }

        Object value = parentSequence.getValue(index);
        String prefix = sequencePrefix(parentSequence, index);

        return value != null
                ? prefix + ": " + truncate(value.toString())
                : prefix + ": [ ... ]";
    }

    private static @NotNull String sequencePrefix(@NotNull ConfigSequence sequence, int index) {
        String base = sequence.isRoot() ? "" : sequence.getKey();
        return base + "[" + (index + 1) + "]";
    }

    private static @NotNull String truncate(@NotNull String value) {
        return value.length() > MAX_VALUE_LENGTH
                ? value.substring(0, MAX_VALUE_LENGTH) + "..."
                : value;
    }

    private static @NotNull String keyedLine(@Nullable String key, @NotNull String value) {
        return key != null ? key + ": " + value : value;
    }

    private static @NotNull String containerPlaceholder(@NotNull ConfigField field, @NotNull String openClose) {
        if (field instanceof ConfigSection section && section.isRoot()) {
            return openClose;
        }
        if (field instanceof ConfigSequence sequence && sequence.isRoot()) {
            return openClose;
        }
        if (field instanceof KeyedField keyedField) {
            return keyedField.getKey() + ": " + openClose;
        }
        if (field instanceof KeylessField keylessField && keylessField.getParent() instanceof KeyedSequence parentSequence) {
            return parentSequence.getKey() + "[" + (keylessField.getIndex() + 1) + "]: " + openClose;
        }
        return openClose;
    }

}