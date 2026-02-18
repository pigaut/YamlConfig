package io.github.pigaut.yaml.amount;

import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public interface Amount {

    int intValue();

    double doubleValue();

    boolean match(double amount);

    boolean test(DoublePredicate predicate);

    Amount transform(DoubleFunction<Double> mapper);

    static Amount fixed(double amount) {
        return new FixedAmount(amount);
    }

    static Amount between(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Minimum value must be less than maximum value.");
        }
        return new RangedAmount(min, max);
    }

    static Amount random(double... values) {
        if (values.length < 1) {
            throw new IllegalArgumentException("Values must contain at least one element.");
        }
        return new RandomAmount(values);
    }

    static Amount random(@NotNull List<Double> values) {
        Preconditions.checkNotNull(values, "Values cannot be null.");
        final double[] arrayValues = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            final Double value = values.get(i);
            Preconditions.checkNotNull(values, "Values must not contain null elements.");
            arrayValues[i] = value;
        }
        return random(arrayValues);
    }

    static @NotNull Amount fromString(String string) {
        try {
            return ParseUtil.parseAmount(string);
        } catch (StringParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    Amount ZERO = Amount.fixed(0);
    Amount ONE = Amount.fixed(1);

    Amount ANY = new Amount() {
        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }

        @Override
        public boolean match(double amount) {
            return true;
        }

        @Override
        public boolean test(DoublePredicate predicate) {
            return predicate.test(0);
        }

        @Override
        public Amount transform(DoubleFunction<Double> mapper) {
            return this;
        }
    };

}
