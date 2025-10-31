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

        KeyedSection newSection = new KeyedSection(parent, key);
        parent.addNode(newSection);
        return newSection;
    }

    @Override
    public Sequence createSequence(@NotNull Branch branch) {
        final Section parent = branch.convertToSection();
        final String key = getExistingKeyOrDefault(parent);
        final Object field = parent.getNode(key);

        if (field instanceof Sequence foundSequence) {
            return foundSequence;
        }

        KeyedSequence newSequence = new KeyedSequence(parent, key);
        parent.addNode(newSequence);
        return newSequence;
    }

    @Override
    public Scalar createScalar(@NotNull Branch branch, @NotNull Object value) {
        final Section section = branch.convertToSection();

        String key = keys[0];
        for (String keyAlias : keys) {
            if (section.getNode(keyAlias) != null) {
                key = keyAlias;
                break;
            }
        }

        KeyedScalar newScalar = new KeyedScalar(section, key, value);
        section.addNode(newScalar);
        return newScalar;
    }

    @Override
    public void remove(@NotNull Branch branch) {
        final Section section = branch.convertToSection();
        for (String key : keys) {
            section.removeNode(key);
        }
    }

}
