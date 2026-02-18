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
        Section section = branch.convertToSection();
        Object field = section.getNode(key);

        if (field instanceof Section foundSection) {
            return foundSection;
        }

        KeyedSection newSection = new KeyedSection(section, key);
        section.addNode(newSection);
        return newSection;
    }

    @Override
    public Sequence createSequence(@NotNull Branch branch) {
        Section section = branch.convertToSection();
        Object field = section.getNode(key);

        if (field instanceof Sequence foundSequence) {
            return foundSequence;
        }

        KeyedSequence newSequence = new KeyedSequence(section, key);
        section.addNode(newSequence);
        return newSequence;
    }

    @Override
    public Scalar createScalar(@NotNull Branch branch, @NotNull Object value) {
        Section section = branch.convertToSection();
        KeyedScalar newScalar = new KeyedScalar(section, key, value);
        section.addNode(newScalar);
        return newScalar;
    }

    @Override
    public void remove(@NotNull Branch branch) {
        Section section = branch.convertToSection();
        section.removeNode(key);
    }

}
