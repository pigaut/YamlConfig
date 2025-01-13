package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.loader.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.parser.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;

public abstract class Scalar extends Field implements ConfigScalar {

    private Object value;
    private ScalarStyle scalarStyle;

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
    public boolean toBoolean() throws InvalidConfigurationException {
        return asBoolean().orElseThrow(() -> new InvalidConfigurationException(this, "Expected a boolean but found: '" + this.toString() + "'"));
    }

    @Override
    public char toCharacter() throws InvalidConfigurationException {
        return asCharacter().orElseThrow(() -> new InvalidConfigurationException(this, "Expected a character but found: '" + this.toString() + "'"));
    }

    @Override
    public @NotNull String toString(@NotNull StringFormatter formatter) {
        final String string = this.toString();
        return formatter.format(string);
    }

    @Override
    public int toInteger() throws InvalidConfigurationException {
        return asInteger().orElseThrow(() -> new InvalidConfigurationException(this, "Expected an integer but found: '" + this.toString() + "'"));
    }

    @Override
    public long toLong() throws InvalidConfigurationException {
        return asLong().orElseThrow(() -> new InvalidConfigurationException(this, "Expected a long but found: '" + this.toString() + "'"));
    }

    @Override
    public float toFloat() throws InvalidConfigurationException {
        return asFloat().orElseThrow(() -> new InvalidConfigurationException(this, "Expected a float but found: '" + this.toString() + "'"));
    }

    @Override
    public double toDouble() throws InvalidConfigurationException {
        return asDouble().orElseThrow(() -> new InvalidConfigurationException(this, "Expected a double but found: '" + this.toString() + "'"));
    }

    @Override
    public Optional<Boolean> asBoolean() {
        if (value instanceof Boolean bool) {
            return Optional.of(bool);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Character> asCharacter() {
        if (value instanceof String string && string.length() == 1) {
            return Optional.of(string.charAt(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> asInteger() {
        if (value instanceof Number number) {
            if (number instanceof Byte || number instanceof Short || number instanceof Integer) {
                return Optional.of(number.intValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> asLong() {
        if (value instanceof Number number) {
            if (number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long) {
                return Optional.of(number.longValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Float> asFloat() {
        if (value instanceof Number number) {
            if (number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long || number instanceof Float || number instanceof Double) {
                return Optional.of(number.floatValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Double> asDouble() {
        if (value instanceof Number number) {
            if (number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long || number instanceof Float || number instanceof Double) {
                return Optional.of(number.doubleValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull MappingType getFieldType() {
        return MappingType.SCALAR;
    }

    @Override
    public void clear() {
        value = "";
    }

    @Override
    public <T> T load(Class<T> type) throws InvalidConfigurationException {
        final Configurator configurator = getRoot().getConfigurator();
        final ConfigLoader<? extends T> loader = configurator.getLoader(type);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class type: " + type.getSimpleName());
        }
        this.setProblemDescription(loader.getProblemDescription());
        final T value = loader.loadFromScalar(this);
        this.setProblemDescription(null);
        return value;
    }

    @Override
    public @NotNull ConfigScalar toScalar() throws InvalidConfigurationException {
        return this;
    }

    @Override
    public @NotNull ConfigSection toSection() throws InvalidConfigurationException {
        throw new InvalidConfigurationException(this, "Expected a section but found a value");
    }

    @Override
    public @NotNull ConfigSequence toSequence() throws InvalidConfigurationException {
        throw new InvalidConfigurationException(this, "Expected a list but found a value");
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }

}
