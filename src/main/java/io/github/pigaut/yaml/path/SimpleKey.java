package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

public class SimpleKey implements FieldKey {

    private final String key;

    public SimpleKey(String key) {
        Preconditions.checkArgument(!key.isBlank(), "Key cannot be blank");
        this.key = key;
    }

    @Override
    public ConfigField getField(@NotNull Branch branch) {
        if (branch instanceof Section section) {
            return section.getNode(key);
        }
        return null;
    }

    @Override
    public Section createSection(@NotNull Branch branch) {
        final Section section = branch.convertToSection();
        final Object field = section.getNode(key);

        if (field instanceof Section foundSection) {
            return foundSection;
        }

        final Section createdSection = new KeyedSection(section, key);
        section.putNode(key, createdSection);
        return createdSection;
    }

    @Override
    public Sequence createSequence(@NotNull Branch branch) {
        final Section section = branch.convertToSection();
        final Object field = section.getNode(key);

        if (field instanceof Sequence foundSequence) {
            return foundSequence;
        }

        final Sequence createdSequence = new KeyedSequence(section, key);
        section.putNode(key, createdSequence);
        return createdSequence;
    }

    @Override
    public Scalar createScalar(@NotNull Branch branch, @NotNull Object value) {
        final Section section = branch.convertToSection();
        final KeyedScalar scalar = new KeyedScalar(section, key, value);

        section.putNode(key, scalar);
        return scalar;
    }

    @Override
    public void remove(@NotNull Branch branch) {
        final Section section = branch.convertToSection();
        section.removeNode(key);
    }

}
