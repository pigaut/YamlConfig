package io.github.pigaut.yamlib.config;

import io.github.pigaut.yamlib.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.*;
import org.snakeyaml.engine.v2.exceptions.*;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.representer.*;
import org.snakeyaml.engine.v2.scanner.*;

import java.math.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public class ConfigRepresenter extends BaseRepresenter {

    public static final Pattern MULTILINE_PATTERN = Pattern.compile("\n|\u0085");

    protected Map<Class<? extends Object>, Tag> classTags;
    protected DumpSettings settings;

    public ConfigRepresenter(DumpSettings settings) {
        this.defaultFlowStyle = settings.getDefaultFlowStyle();
        this.defaultScalarStyle = settings.getDefaultScalarStyle();

        this.nullRepresenter = new RepresentNull();
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.representers.put(Character.class, new RepresentString());

        this.parentClassRepresenters.put(Number.class, new RepresentNumber());
        this.parentClassRepresenters.put(List.class, new RepresentList());
        this.parentClassRepresenters.put(Map.class, new RepresentMap());
        this.parentClassRepresenters.put(ConfigSection.class, new RepresentSection());

        classTags = new HashMap<>();
        this.settings = settings;
    }

    protected Tag getTag(Class<?> clazz, Tag defaultTag) {
        return classTags.getOrDefault(clazz, defaultTag);
    }

    @Deprecated
    public Tag addClassTag(Class<? extends Object> clazz, Tag tag) {
        if (tag == null) {
            throw new NullPointerException("Tag must be provided.");
        }
        return classTags.put(clazz, tag);
    }

    protected class RepresentNull implements RepresentToNode {

        public Node representData(Object data) {
            return representScalar(Tag.NULL, "null");
        }
    }

    public class RepresentString implements RepresentToNode {

        public Node representData(Object data) {
            Tag tag = Tag.STR;
            ScalarStyle style = ScalarStyle.PLAIN;
            String value = data.toString();
            if (settings.getNonPrintableStyle() == NonPrintableStyle.BINARY
                    && !StreamReader.isPrintable(value)) {
                tag = Tag.BINARY;
                final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                // sometimes above will just silently fail - it will return incomplete data
                // it happens when String has invalid code points
                // (for example half surrogate character without other half)
                final String checkValue = new String(bytes, StandardCharsets.UTF_8);
                if (!checkValue.equals(value)) {
                    throw new YamlEngineException("invalid string value has occurred");
                }
                value = Base64.getEncoder().encodeToString(bytes);
                style = ScalarStyle.LITERAL;
            }
            // if no other scalar style is explicitly set, use literal style for
            // multiline scalars
            if (defaultScalarStyle == ScalarStyle.PLAIN && MULTILINE_PATTERN.matcher(value).find()) {
                style = ScalarStyle.LITERAL;
            }
            return representScalar(tag, value, style);
        }
    }

    public class RepresentBoolean implements RepresentToNode {

        public Node representData(Object data) {
            String value;
            if (Boolean.TRUE.equals(data)) {
                value = "true";
            } else {
                value = "false";
            }
            return representScalar(Tag.BOOL, value);
        }
    }

    public class RepresentNumber implements RepresentToNode {

        public Node representData(Object data) {
            Tag tag;
            String value;
            if (data instanceof Byte || data instanceof Short || data instanceof Integer
                    || data instanceof Long || data instanceof BigInteger) {
                tag = Tag.INT;
                value = data.toString();
            } else {
                Number number = (Number) data;
                tag = Tag.FLOAT;
                if (number.equals(Double.NaN) || number.equals(Float.NaN)) {
                    value = ".nan";
                } else if (number.equals(Double.POSITIVE_INFINITY)
                        || number.equals(Float.POSITIVE_INFINITY)) {
                    value = ".inf";
                } else if (number.equals(Double.NEGATIVE_INFINITY)
                        || number.equals(Float.NEGATIVE_INFINITY)) {
                    value = "-.inf";
                } else {
                    value = number.toString();
                }
            }
            return representScalar(getTag(data.getClass(), tag), value);
        }
    }

    public class RepresentList implements RepresentToNode {

        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            return representSequence(getTag(data.getClass(), Tag.SEQ), (List<Object>) data,
                    settings.getDefaultFlowStyle());
        }
    }

    public class RepresentMap implements RepresentToNode {

        @SuppressWarnings("unchecked")
        public Node representData(Object data) {
            return representMapping(getTag(data.getClass(), Tag.MAP), (Map<Object, Object>) data,
                    settings.getDefaultFlowStyle());
        }
    }
    
    public class RepresentSection implements RepresentToNode {

        @Override
        public Node representData(Object data) {
            ConfigSection section = (ConfigSection) data;

            return section.isKeyless() ? representSequence(getTag(data.getClass(), Tag.SEQ), section.toList(), section.getFlowStyle()) :
                    representMapping(getTag(data.getClass(), Tag.MAP), section.getNestedFields(), section.getFlowStyle());
        }

    }

}
