package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class IterableMapper implements ConfigMapper.Sequence<Iterable> {

    @Override
    public void mapToSequence(@NotNull ConfigSequence sequence, @NotNull Iterable elements) {
        sequence.clear();
        for (Object element : elements) {
            sequence.add(element);
        }
    }

    @Override
    public void mapToSection(@NotNull ConfigSection section, @NotNull Iterable elements) {
        section.clear();
        int count = 0;
        for (Object element : elements) {
            final String indexAsKey = Integer.toString(count++);
            section.set(indexAsKey, element);
        }
    }

    @Override
    public void mapToScalar(@NotNull ConfigScalar scalar, @NotNull Iterable elements) {
        StringJoiner joiner = new StringJoiner(" ,");
        for (Object element : elements) {
            joiner.add(element.toString());
        }
        scalar.setValue(joiner.toString());
    }

}
