package io.github.pigaut.yaml.amount.config;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.convert.parse.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class AmountLoader implements ConfigLoader<Amount> {

    @Override
    public @Nullable String getProblemDescription() {
        return "invalid amount";
    }

    @Override
    public @NotNull Amount loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
        try {
            return ParseUtil.parseAmount(scalar.toString());
        } catch (StringParseException e) {
            throw new InvalidConfigException(scalar, e.getMessage());
        }
    }

    @Override
    public @NotNull Amount loadFromSection(@NotNull ConfigSection section) throws InvalidConfigException {
        final double min = section.getDouble("min|minimum").orThrow();
        final double max = section.getDouble("max|maximum")
                .require(Requirements.min(min), "Maximum value must be greater than the minimum value")
                .orThrow();
        return Amount.between(min, max);
    }

    @Override
    public @NotNull Amount loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigException {
        final List<Double> values = sequence.toDoubleList().orThrow();

        if (values.isEmpty()) {
            throw new InvalidConfigException(sequence, "Casual amount needs at least two elements");
        }

        return values.size() == 1 ? Amount.fixed(values.get(0)) : Amount.random(values);
    }

}
