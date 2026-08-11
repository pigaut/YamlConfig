package io.github.pigaut.yaml.node.line.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

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
    public @NotNull String getKey(@NotNull CaseStyle style) {
        return getKey();
    }

    @Override
    public boolean hasErrors() {
        return line.hasErrors();
    }

    @Override
    public boolean hasWarnings() {
        return line.hasWarnings();
    }

    @Override
    public int getErrorCount() {
        return line.getErrorCount();
    }

    @Override
    public int getWarningCount() {
        return line.getWarningCount();
    }

    @Override
    public @NotNull List<ConfigException> getErrors() {
        return line.getErrors();
    }

    @Override
    public @NotNull List<ConfigException> getWarnings() {
        return line.getWarnings();
    }

    @Override
    public void collectError(@NotNull ConfigException error) {
        line.collectError(error);
    }

    @Override
    public void collectWarning(@NotNull ConfigException warning) {
        line.collectWarning(warning);
    }

    @Override
    public void collectAll(@NotNull ErrorCollector other) {
        line.collectAll(other);
    }

    @Override
    public void clearErrors() {
        line.clearErrors();
    }

    @Override
    public void clearWarnings() {
        line.clearWarnings();
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
        if (value instanceof String string) {
            Integer parsed = ParseUtil.parseIntegerOrNull(string);
            if (parsed != null) {
                return ConfigOptional.of(line, parsed);
            }
        }
        return ConfigOptional.invalid(line, "Missing an integer value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Long> toLong() {
        if (ScalarUtil.isLong(value)) {
            return ConfigOptional.of(line, ((Number) value).longValue());
        }
        if (value instanceof String string) {
            Long parsed = ParseUtil.parseLongOrNull(string);
            if (parsed != null) {
                return ConfigOptional.of(line, parsed);
            }
        }
        return ConfigOptional.invalid(line, "Missing a long value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Double> toDouble() {
        if (ScalarUtil.isDouble(value)) {
            return ConfigOptional.of(line, ((Number) value).doubleValue());
        }
        if (value instanceof String string) {
            Double parsed = ParseUtil.parseDoubleOrNull(string);
            if (parsed != null) {
                return ConfigOptional.of(line, parsed);
            }
        }
        return ConfigOptional.invalid(line, "Missing a double value at position: " + getPosition());
    }

    @Override
    public ConfigOptional<Float> toFloat() {
        if (ScalarUtil.isFloat(value)) {
            return ConfigOptional.of(line, ((Number) value).floatValue());
        }
        if (value instanceof String string) {
            Double parsed = ParseUtil.parseDoubleOrNull(string);
            if (parsed != null) {
                return ConfigOptional.of(line, parsed.floatValue());
            }
        }
        return ConfigOptional.invalid(line, "Missing a float value at position: " + getPosition());
    }

}
