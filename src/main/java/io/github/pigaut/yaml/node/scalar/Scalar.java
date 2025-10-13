package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.configurator.map.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.*;
import io.github.pigaut.yaml.optional.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

public abstract class Scalar extends Field implements ConfigScalar {

    private Object value;
    private ScalarStyle scalarStyle;
    private @Nullable Line line = null;

    protected Scalar(@NotNull Object value) {
        this(value, ScalarStyle.PLAIN);
    }

    protected Scalar(@NotNull Object value, @NotNull ScalarStyle scalarStyle) {
        setValue(value);
        this.scalarStyle = scalarStyle;
    }

    @Override
    public boolean contains(String value) {
        String string = toString();
        return string.contains(value);
    }

    @Override
    public @NotNull Object getValue() {
        return value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value != null) {
            if (!YamlConfig.isScalarType(value.getClass())) {
                throw new IllegalArgumentException("Value is not a scalar");
            }
            this.value = value;
        }
        else {
            this.value = "";
        }

        if (line != null) {
            line.updateLine(value.toString());
        }
    }

    @Override
    public @NotNull ScalarStyle getScalarStyle() {
        return scalarStyle;
    }

    @Override
    public void setScalarStyle(@NotNull ScalarStyle scalarStyle) {
        this.scalarStyle = scalarStyle;
    }

    @Override
    public @NotNull String toString(@NotNull StringFormatter formatter) {
        final String string = this.toString();
        return formatter.format(string);
    }

    @Override
    public ConfigOptional<Boolean> toBoolean() {
        if (ScalarUtil.isBoolean(value)) {
            return ConfigOptional.of(this, (Boolean) value);
        }
        return ConfigOptional.invalid(this, "Expected a boolean but found: " + this);
    }

    @Override
    public ConfigOptional<Character> toCharacter() {
        if (ScalarUtil.isCharacter(value)) {
            return ConfigOptional.of(this, value.toString().charAt(0));
        }
        return ConfigOptional.invalid(this, "Expected a character but found: " + this);
    }

    @Override
    public ConfigOptional<Integer> toInteger() {
        if (ScalarUtil.isInteger(value)) {
            return ConfigOptional.of(this, ((Number) value).intValue());
        }
        return ConfigOptional.invalid(this, "Expected an integer but found: " + this);
    }

    @Override
    public ConfigOptional<Long> toLong() {
        if (ScalarUtil.isLong(value)) {
            return ConfigOptional.of(this, ((Number) value).longValue());
        }
        return ConfigOptional.invalid(this, "Expected a long but found: " + this);
    }

    @Override
    public ConfigOptional<Float> toFloat() {
        if (ScalarUtil.isFloat(value)) {
            return ConfigOptional.of(this, ((Number) value).floatValue());
        }
        return ConfigOptional.invalid(this, "Expected a float but found: " + this);
    }

    @Override
    public ConfigOptional<Double> toDouble() {
        if (ScalarUtil.isDouble(value)) {
            return ConfigOptional.of(this, ((Number) value).doubleValue());
        }
        return ConfigOptional.invalid(this, "Expected a double but found: " + this);
    }

    @Override
    public @NotNull FieldType getFieldType() {
        return FieldType.SCALAR;
    }

    @Override
    public void clear() {
        setValue("");
    }

    @Override
    public <T> ConfigOptional<T> load(Class<T> classType) {
        final ConfigRoot root = this.getRoot();
        final Configurator configurator = root.getConfigurator();

        final ConfigLoader<? extends T> loader = configurator.getLoader(classType);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class type: " + classType.getSimpleName());
        }

        final String problemDescription = loader.getProblemDescription();
        root.addProblem(problemDescription);

        try {
            return ConfigOptional.of(this, loader.loadFromScalar(this));
        }
        catch (InvalidConfigurationException e) {
            return ConfigOptional.invalid(e);
        }
        finally {
            root.removeProblem(problemDescription);
        }
    }

    @Override
    public <T> void map(T value) {
        final Configurator configurator = getRoot().getConfigurator();
        @SuppressWarnings("unchecked")
        var mapper = (ConfigMapper<? super T>) configurator.getMapper(value.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("No config mapper found for class: " + value.getClass().getSimpleName());
        }
        mapper.mapToScalar(this, value);
    }

    @Override
    public ConfigOptional<ConfigScalar> toScalar() {
        return ConfigOptional.of(this);
    }

    @Override
    public ConfigOptional<ConfigSection> toSection() {
        return ConfigOptional.invalid(this, "Expected a section but found a value");
    }

    @Override
    public ConfigOptional<ConfigSequence> toSequence() {
        return ConfigOptional.invalid(this, "Expected a sequence (list) but found a value");
    }

    @Override
    public ConfigLine toLine() {
        return line != null ? line : (line = new Line(this));
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }

}
