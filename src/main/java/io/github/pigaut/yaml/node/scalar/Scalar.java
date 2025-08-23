package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.line.*;
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
    public @NotNull Object getValue() {
        return value;
    }

    @Override
    public void setValue(@NotNull Object value) {
        Preconditions.checkNotNull(value, "Value cannot be null");
        Preconditions.checkArgument(YamlConfig.isScalarType(value.getClass()), "Value is not a scalar");
        this.value = value;
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
        return ConfigOptional.empty(this, "Expected a boolean but found: " + this);
    }

    @Override
    public ConfigOptional<Character> toCharacter() {
        if (ScalarUtil.isCharacter(value)) {
            return ConfigOptional.of(this, value.toString().charAt(0));
        }
        return ConfigOptional.empty(this, "Expected a character but found: " + this);
    }

    @Override
    public ConfigOptional<Integer> toInteger() {
        if (ScalarUtil.isInteger(value)) {
            return ConfigOptional.of(this, ((Number) value).intValue());
        }
        return ConfigOptional.empty(this, "Expected an integer but found: " + this);
    }

    @Override
    public ConfigOptional<Long> toLong() {
        if (ScalarUtil.isLong(value)) {
            return ConfigOptional.of(this, ((Number) value).longValue());
        }
        return ConfigOptional.empty(this, "Expected a long but found: " + this);
    }

    @Override
    public ConfigOptional<Float> toFloat() {
        if (ScalarUtil.isFloat(value)) {
            return ConfigOptional.of(this, ((Number) value).floatValue());
        }
        return ConfigOptional.empty(this, "Expected a float but found: " + this);
    }

    @Override
    public ConfigOptional<Double> toDouble() {
        if (ScalarUtil.isDouble(value)) {
            return ConfigOptional.of(this, ((Double) value).doubleValue());
        }
        return ConfigOptional.empty(this, "Expected a double but found: " + this);
    }

    @Override
    public @NotNull FieldType getFieldType() {
        return FieldType.SCALAR;
    }

    @Override
    public void clear() {
        value = "";
    }

    @Override
    public <T> ConfigOptional<T> load(Class<T> type) {
        final ConfigRoot root = this.getRoot();
        final Configurator configurator = root.getConfigurator();

        final ConfigLoader<? extends T> loader = configurator.getLoader(type);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class type: " + type.getSimpleName());
        }

        final String problemDescription = loader.getProblemDescription();
        root.addProblem(problemDescription);

        try {
            return ConfigOptional.of(this, loader.loadFromScalar(this));
        } catch (InvalidConfigurationException e) {
            return ConfigOptional.empty(e);
        }
        finally {
            root.removeProblem(problemDescription);
        }
    }

    @Override
    public ConfigOptional<ConfigScalar> toScalar() {
        return ConfigOptional.of(this);
    }

    @Override
    public ConfigOptional<ConfigSection> toSection() {
        return ConfigOptional.empty(this, "Expected a section but found a value");
    }

    @Override
    public ConfigOptional<ConfigSequence> toSequence() {
        return ConfigOptional.empty(this, "Expected a sequence (list) but found a value");
    }

    @Override
    public ConfigOptional<ConfigBranch> toBranch() {
        return ConfigOptional.empty(this, "Expected a branch but found a value");
    }

    @Override
    public ConfigLine toLine() {
        if (line != null) {
            return line;
        }

        return (line = new Line(this, toString()));
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }

}
