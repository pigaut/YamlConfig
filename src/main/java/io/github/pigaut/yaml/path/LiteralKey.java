package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

public class LiteralKey implements FieldKey {

    private final String key;

    public LiteralKey(@NotNull String key) {
        Preconditions.checkArgument(!key.isBlank(), "Key cannot be blank");
        this.key = key;
    }

    @Override
    public @Nullable ConfigField getField(@NotNull Branch branch) {
        if (branch instanceof Section section) {
            return section.getNode(key);
        }
        return null;
    }

    @Override
    public @NotNull Section createSection(@NotNull Branch branch, boolean attached) {
        Section section = branch.convertToSection();
        Object field = section.getNode(key);

        if (field instanceof Section foundSection) {
            return foundSection;
        }

        KeyedSection newSection = new KeyedSection(section, key);
        if (attached) {
            section.addNode(newSection);
        }

        return newSection;
    }

    @Override
    public @NotNull Sequence createSequence(@NotNull Branch branch, boolean attached) {
        Section section = branch.convertToSection();
        Object field = section.getNode(key);

        if (field instanceof Sequence foundSequence) {
            return foundSequence;
        }

        KeyedSequence newSequence = new KeyedSequence(section, key);
        if (attached) {
            section.addNode(newSequence);
        }

        return newSequence;
    }

    @Override
    public @NotNull Scalar createScalar(@NotNull Branch branch, @NotNull Object value, boolean attached) {
        Section section = branch.convertToSection();
        KeyedScalar newScalar = new KeyedScalar(section, key, value);
        if (attached) {
            section.addNode(newScalar);
        }

        return newScalar;
    }

    @Override
    public void removeField(@NotNull Branch branch) {
        Section section = branch.convertToSection();
        section.removeNode(key);
    }

}
