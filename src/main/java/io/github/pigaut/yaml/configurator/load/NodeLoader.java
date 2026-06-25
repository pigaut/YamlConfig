package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.*;

public class NodeLoader implements ConfigLoader<Node> {

    @Override
    public @NotNull Node loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
        Tag tag = ScalarUtil.getTag(scalar.getValue());
        String value = scalar.toString();

        ScalarNode scalarNode = new ScalarNode(tag, value, scalar.getScalarStyle());
        scalarNode.setInLineComments(scalar.getInLineComments());
        scalarNode.setBlockComments(scalar.getBlockComments());

        return scalarNode;
    }

    @Override
    public @NotNull Node loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
        List<NodeTuple> nodeTuples = new ArrayList<>();
        for (KeyedField field : section.getNestedFields()) {
            Node keyNode = field.getKeyAs(Node.class).orElse(null);
            Node valueNode = field.get(Node.class).orElse(null);
            if (keyNode != null && valueNode != null) {
                nodeTuples.add(new NodeTuple(keyNode, valueNode));
            }
        }

        MappingNode mappingNode = new MappingNode(Tag.MAP, nodeTuples, section.getFlowStyle());
        mappingNode.setBlockComments(section.getBlockComments());
        mappingNode.setInLineComments(section.getInLineComments());

        return mappingNode;
    }

    @Override
    public @NotNull Node loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
        List<Node> nodes = new ArrayList<>();
        for (ConfigField field : sequence.getNestedFields()) {
            Node node = field.get(Node.class).orElse(null);
            if (node != null) {
                nodes.add(node);
            }
        }

        SequenceNode sequenceNode = new SequenceNode(Tag.SEQ, nodes, sequence.getFlowStyle());
        sequenceNode.setInLineComments(sequence.getInLineComments());
        sequenceNode.setBlockComments(sequence.getBlockComments());

        return sequenceNode;
    }

}
