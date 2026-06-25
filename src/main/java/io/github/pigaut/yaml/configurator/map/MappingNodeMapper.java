package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.*;

public class MappingNodeMapper implements ConfigMapper<MappingNode> {

    @Override
    public @NotNull FieldType getDefaultMappingType() {
        return FieldType.SECTION;
    }

    @Override
    public boolean clearExistingFields() {
        return true;
    }

    @Override
    public void mapToSection(@NotNull ConfigSection section, @NotNull MappingNode mappingNode) {
        for (NodeTuple nodeTuple : mappingNode.getValue()) {
            section.add(nodeTuple);
//            String key = YamlConfig.getNodeKey(nodeTuple);
//            Node valueNode = nodeTuple.getValueNode();
//            section.set(key, valueNode);
        }

        section.setInLineComments(mappingNode.getInLineComments());
        section.setBlockComments(mappingNode.getBlockComments());
    }

}
