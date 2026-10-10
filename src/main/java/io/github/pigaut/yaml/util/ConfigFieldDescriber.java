package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;

public final class ConfigFieldDescriber {

    private static final int MAX_VALUE_LENGTH = 35;

    private ConfigFieldDescriber() {
    }

    public static @Nullable String describe(@NotNull ConfigField field) {
        return describe(field, null);
    }

    public static @Nullable String describe(@NotNull ConfigField field, @Nullable Object key) {
        if (field instanceof Scalar scalar) {
            return describeScalar(scalar);
        }
        if (field instanceof Sequence sequence) {
            return describeSequence(sequence, key);
        }
        if (field instanceof Section section) {
            return describeSection(section, key);
        }
        return null;
    }

    private static @NotNull String describeScalar(@NotNull Scalar scalar) {
        String label = labelFor(scalar);
        String value = truncate(scalar.toString());
        return label != null ? label + ": " + value : value;
    }

    private static @NotNull String describeSequence(@NotNull Sequence sequence, @Nullable Object rawKey) {
        if (rawKey instanceof Integer index) {
            ConfigField element = sequence.getNode(index);
            if (element != null) {
                String described = describe(element);
                if (described != null) {
                    return described;
                }
            }
        }
        return describeEmptyBranch(sequence, "[ ... ]");
    }

    private static @NotNull String describeSection(@NotNull Section section, @Nullable Object rawKey) {
        if (rawKey != null) {
            String key = rawKey.toString();
            if (section.isScalar(key)) {
                Object value = section.getNode(key);
                if (value != null) {
                    return key + ": " + truncate(value.toString());
                }
            }
        }
        return describeEmptyBranch(section, "{ ... }");
    }

    private static @NotNull String describeEmptyBranch(@NotNull ConfigBranch branch, @NotNull String openClose) {
        String label = labelFor(branch);
        return label != null ? label + ": " + openClose : openClose;
    }

    // Computes this field's own label: its key if keyed, its index path if keyless
    // (walking up through any nested keyless sequences), or null at the root.
    private static @Nullable String labelFor(@NotNull ConfigField field) {
        if (field.isRoot()) {
            return null;
        }
        if (field instanceof KeyedField keyedField) {
            return keyedField.getKey();
        }
        if (field instanceof KeylessField keylessField) {
            return indexPath(keylessField);
        }
        return null;
    }

    // Walks up through consecutive keyless sequences collecting indices,
    // then prefixes with whatever key (if any) sits above them.
    private static @NotNull String indexPath(@NotNull KeylessField field) {
        List<Integer> indices = new ArrayList<>();
        ConfigField current = field;

        while (current instanceof KeylessField klf) {
            indices.add(0, klf.getIndex() + 1);
            current = klf.getParent();
        }

        String base = current instanceof KeyedField keyedField ? keyedField.getKey() : "";
        StringBuilder result = new StringBuilder(base);
        for (int index : indices) {
            result.append("[").append(index).append("]");
        }
        return result.toString();
    }

    private static @NotNull String truncate(@NotNull String value) {
        return value.length() > MAX_VALUE_LENGTH
                ? value.substring(0, MAX_VALUE_LENGTH) + "..."
                : value;
    }

}