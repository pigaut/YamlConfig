package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PathIterator implements Iterator<Object> {

    private final List<FieldKey> keys;
    private Branch currentBranch;
    private int pointer = 0;

    public PathIterator(Branch branch, List<FieldKey> keys) {
        Preconditions.checkArgument(!keys.isEmpty(), "Keys must have at least one element");
        this.currentBranch = branch;
        this.keys = keys;
    }

    public static PathIterator of(@NotNull Branch parent, @NotNull String path) {
        return new PathIterator(parent, FieldKey.keysOf(path));
    }

    public boolean hasNext() {
        return pointer < keys.size();
    }

    @Nullable
    public ConfigField next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more keys in the path.");
        }

        if (currentBranch == null) {
            return null;
        }

        final ConfigField field = keys.get(pointer++).getField(currentBranch);

        if (field instanceof Branch branch) {
            currentBranch = branch;
        }

        return field;
    }

    @NotNull
    public Branch nextBranch() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more keys in the path.");
        }

        if (currentBranch == null) {
            throw new IllegalStateException("Current branch is null.");
        }

        final FieldKey currentKey = keys.get(pointer);
        pointer++;

        final ConfigField existingField = currentKey.getField(currentBranch);
        if (existingField instanceof Branch existingBranch) {
            currentBranch = existingBranch;
            return existingBranch;
        }

        if (!isLast() && getNextKey() instanceof IndexKey) {
            currentBranch = currentKey.createSequence(currentBranch);
            return currentBranch;
        }

        currentBranch = currentKey.createSection(currentBranch);
        return currentBranch;
    }

    public boolean isLast() {
        return pointer + 1 >= keys.size();
    }

    public @Nullable Branch getCurrentBranch() {
        return currentBranch;
    }

    public FieldKey getNextKey() {
        return keys.get(pointer + 1);
    }

    public FieldKey getCurrentKey() {
        return keys.get(pointer);
    }

    public FieldKey getLastKey() {
        return keys.get(keys.size() - 1);
    }

}
