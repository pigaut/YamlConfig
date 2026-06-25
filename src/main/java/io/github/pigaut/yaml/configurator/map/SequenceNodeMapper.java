package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.nodes.*;

public class SequenceNodeMapper implements ConfigMapper<SequenceNode> {

    @Override
    public @NotNull FieldType getDefaultMappingType() {
        return FieldType.SEQUENCE;
    }

    @Override
    public boolean clearExistingFields() {
        return true;
    }

    @Override
    public void mapToSequence(@NotNull ConfigSequence sequence, @NotNull SequenceNode sequenceNode) {
        for (Node node : sequenceNode.getValue()) {
            sequence.add(node);
        }

        sequence.setInLineComments(sequenceNode.getInLineComments());
        sequence.setBlockComments(sequenceNode.getBlockComments());
    }

}
