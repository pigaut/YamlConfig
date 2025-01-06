package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.*;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.representer.*;
import org.snakeyaml.engine.v2.scanner.*;

import java.math.*;
import java.util.*;

public class ConfigRepresenter extends StandardRepresenter {

    public ConfigRepresenter(DumpSettings settings) {
        super(settings);
        this.defaultFlowStyle = settings.getDefaultFlowStyle();
        this.defaultScalarStyle = settings.getDefaultScalarStyle();

        this.representers.clear();
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.representers.put(Character.class, new RepresentString());

        this.parentClassRepresenters.clear();
        this.parentClassRepresenters.put(Number.class, new RepresentNumber());
        this.parentClassRepresenters.put(List.class, new RepresentList());
        this.parentClassRepresenters.put(Map.class, new RepresentMap());
        this.parentClassRepresenters.put(Section.class, new RepresentSection());
        this.parentClassRepresenters.put(Sequence.class, new RepresentSequence());
        this.parentClassRepresenters.put(Scalar.class, new RepresentScalar());
    }

    protected @NotNull Tag getScalarTag(Object scalar) {
        if (scalar instanceof Boolean) {
            return Tag.BOOL;
        }

        if (scalar instanceof Character || scalar instanceof String) {
            final String string = scalar.toString();
            if (settings.getNonPrintableStyle() == NonPrintableStyle.BINARY
                    && !StreamReader.isPrintable(string)) {
                return Tag.BINARY;
            }
            else {
                return Tag.STR;
            }
        }

        if (scalar instanceof Byte || scalar instanceof Short || scalar instanceof Integer
                || scalar instanceof Long || scalar instanceof BigInteger) {
            return Tag.INT;
        }

        return Tag.FLOAT;
    }

    public class RepresentSection implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Section section = (Section) data;
            return representMapping(Tag.MAP, section.toMap(), section.getFlowStyle());
        }
    }

    public class RepresentSequence implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Sequence sequence = (Sequence) data;
            return representSequence(Tag.SEQ, sequence.toList(), sequence.getFlowStyle());
        }
    }

    public class RepresentScalar implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            ConfigScalar scalar = (ConfigScalar) data;
            return representScalar(getScalarTag(scalar.getValue()), scalar.toString(), scalar.getScalarStyle());
        }
    }

}
