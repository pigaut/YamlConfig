package io.github.pigaut.yaml.configurator.map;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.nodes.*;

public class ScalarNodeMapper implements ConfigMapper<ScalarNode> {

    @Override
    public @NotNull FieldType getDefaultMappingType() {
        return FieldType.SCALAR;
    }

    @Override
    public boolean clearExistingFields() {
        return true;
    }

    @Override
    public void mapToScalar(@NotNull ConfigScalar scalar, @NotNull ScalarNode scalarNode) {
        Object parsedValue = ParseUtil.parseAsScalar(scalarNode.getValue());
        scalar.setValue(parsedValue);
        scalar.setBlockComments(scalarNode.getBlockComments());
        scalar.setInLineComments(scalarNode.getInLineComments());
    }

}
