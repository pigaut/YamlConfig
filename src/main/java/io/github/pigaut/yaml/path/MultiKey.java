package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

public class MultiKey implements FieldKey {

    private final String[] keys;

    public MultiKey(String[] keys) {
        Preconditions.checkArgument(keys.length > 0, "Keys cannot be empty");
        this.keys = keys;
    }

    @Override
    public ConfigField getField(@NotNull Branch branch) {
        if (branch instanceof Section parent) {
            for (String key : keys) {
                final ConfigField field = parent.getNode(key);
                if (field != null) {
                    return field;
                }
            }
        }
        return null;
    }

    private String getExistingKeyOrDefault(Section section) {
        for (String key : keys) {
            if (section.getNode(key) != null) {
                return key;
            }
        }
        return keys[0];
    }

    @Override
    public Section createSection(@NotNull Branch branch) {
        final Section parent = branch.convertToSection();
        final String key = getExistingKeyOrDefault(parent);
        final Object field = parent.getNode(key);

        if (field instanceof Section foundSection) {
            return foundSection;
        }

        final Section createdSection = new KeyedSection(parent, key);
        parent.putNode(key, createdSection);
        return createdSection;
    }

    @Override
    public Sequence createSequence(@NotNull Branch branch) {
        final Section parent = branch.convertToSection();
        final String key = getExistingKeyOrDefault(parent);
        final Object field = parent.getNode(key);

        if (field instanceof Sequence foundSequence) {
            return foundSequence;
        }

        final Sequence createdSequence = new KeyedSequence(parent, key);
        parent.putNode(key, createdSequence);
        return createdSequence;
    }

    @Override
    public void set(@NotNull Branch branch, @NotNull Object value) {
        final Section section = branch.convertToSection();

        String key = keys[0];
        for (String keyAlias : keys) {
            if (section.getNode(keyAlias) != null) {
                key = keyAlias;
                break;
            }
        }

        section.putNode(key, new KeyedScalar(section, key, value));
    }

    @Override
    public void remove(@NotNull Branch branch) {
        final Section section = branch.convertToSection();
        for (String key : keys) {
            section.removeNode(key);
        }
    }

}
