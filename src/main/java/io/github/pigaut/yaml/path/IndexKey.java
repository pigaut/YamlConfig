package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

public class IndexKey implements FieldKey {

    private final int index;

    public IndexKey(int index) {
        Preconditions.checkArgument(index >= 0, "Index cannot be a negative number");
        this.index = index;
    }

    public static IndexKey fromString(String key) {
        try {
            return new IndexKey(Integer.parseInt(key));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Key is not an integer");
        }
    }

    @Override
    public ConfigField getField(@NotNull Branch branch) {
        if (branch instanceof Sequence sequence) {
            return sequence.getNode(index);
        }
        return null;
    }

    @Override
    public Section createSection(@NotNull Branch branch) {
        final Sequence sequence = branch.convertToSequence();
        final ConfigField field = sequence.getNode(index);

        if (field instanceof Section foundSection) {
            return foundSection;
        }

        KeylessSection newSection = new KeylessSection(sequence, index);
        sequence.add(newSection);
        return newSection;
    }

    @Override
    public Sequence createSequence(@NotNull Branch branch) {
        final Sequence section = branch.convertToSequence();
        final ConfigField field = section.getNode(index);

        if (field instanceof Sequence foundSequence) {
            return foundSequence;
        }

        KeylessSequence newSequence = new KeylessSequence(section, index);
        section.addNode(newSequence);
        return newSequence;
    }

    @Override
    public Scalar createScalar(@NotNull Branch branch, @NotNull Object value) {
        Sequence sequence = branch.convertToSequence();
        KeylessScalar scalar = new KeylessScalar(sequence, index, value);
        sequence.addNode(scalar);
        return scalar;
    }

    @Override
    public void remove(@NotNull Branch branch) {
        final Sequence sequence = branch.convertToSequence();
        sequence.remove(index);
    }

}
