package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.nodes.*;

public class NodeTupleMapper implements ConfigMapper<NodeTuple> {

    @Override
    public @NotNull FieldType getDefaultMappingType(NodeTuple nodeTuple) {
        Node valueNode = nodeTuple.getValueNode();
        if (valueNode instanceof MappingNode) {
            return FieldType.SECTION;
        }
        if (valueNode instanceof SequenceNode) {
            return FieldType.SEQUENCE;
        }
        return FieldType.SCALAR;
    }

    @Override
    public @NotNull String createKey(@NotNull NodeTuple keyNode) {
        return YamlConfig.getNodeKey(keyNode);
    }

    @Override
    public void mapToScalar(@NotNull ConfigScalar scalar, @NotNull NodeTuple nodeTuple) {
        scalar.map(nodeTuple.getValueNode());
        if (scalar instanceof KeyedField keyedField) {
            Node keyNode = nodeTuple.getKeyNode();
            ConfigScalar scalarKey = keyedField.getKeyScalar();
            scalarKey.setBlockComments(keyNode.getBlockComments());
            scalarKey.setInLineComments(keyNode.getInLineComments());
        }
    }

    @Override
    public void mapToSection(@NotNull ConfigSection section, @NotNull NodeTuple nodeTuple) {
        section.map(nodeTuple.getValueNode());
        if (section instanceof KeyedField keyedField) {
            Node keyNode = nodeTuple.getKeyNode();
            ConfigScalar sectionKey = keyedField.getKeyScalar();
            sectionKey.setBlockComments(keyNode.getBlockComments());
            sectionKey.setInLineComments(keyNode.getInLineComments());
        }
    }

    @Override
    public void mapToSequence(@NotNull ConfigSequence sequence, @NotNull NodeTuple nodeTuple) {
        sequence.map(nodeTuple.getValueNode());
        if (sequence instanceof KeyedField keyedField) {
            Node keyNode = nodeTuple.getKeyNode();
            ConfigScalar keyScalar = keyedField.getKeyScalar();
            keyScalar.setBlockComments(keyNode.getBlockComments());
            keyScalar.setInLineComments(keyNode.getInLineComments());
        }
    }

}
