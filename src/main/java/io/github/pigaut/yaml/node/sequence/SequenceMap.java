package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.node.*;
import org.jetbrains.annotations.*;

@ApiStatus.Internal
public interface SequenceMap {

    @Nullable KeylessField getNode(int index);

    void addNode(KeylessField node);

}
