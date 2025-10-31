package io.github.pigaut.yaml.node.line.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.optional.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

public class KeylessLineScalar extends LineScalar implements KeylessField {

    private int index;

    public KeylessLineScalar(ConfigLine line, int index, Object value) {
        super(line, value);
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getPosition() {
        return index + 1;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public @NotNull String getKey() {
        return "[" + index + "]";
    }

    @Override
    public ConfigOptional<Boolean> toBoolean() {
        if (ScalarUtil.isBoolean(value)) {
            return ConfigOptional.of(line, (Boolean) value);
        }
        return ConfigOptional.invalid(line, "Missing a boolean value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Character> toCharacter() {
        if (ScalarUtil.isCharacter(value)) {
            return ConfigOptional.of(line, value.toString().charAt(0));
        }
        return ConfigOptional.invalid(line, "Missing a character value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Integer> toInteger() {
        if (ScalarUtil.isInteger(value)) {
            return ConfigOptional.of(line, ((Number) value).intValue());
        }
        return ConfigOptional.invalid(line, "Missing an integer value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Long> toLong() {
        if (ScalarUtil.isLong(value)) {
            return ConfigOptional.of(line, ((Number) value).longValue());
        }
        return ConfigOptional.invalid(line, "Missing a long value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Float> toFloat() {
        if (ScalarUtil.isFloat(value)) {
            return ConfigOptional.of(line, ((Number) value).floatValue());
        }
        return ConfigOptional.invalid(line, "Missing a float value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Double> toDouble() {
        if (ScalarUtil.isDouble(value)) {
            return ConfigOptional.of(line, ((Number) value).doubleValue());
        }
        return ConfigOptional.invalid(line, "Missing a double value at position: " + getPosition());
    }

}
