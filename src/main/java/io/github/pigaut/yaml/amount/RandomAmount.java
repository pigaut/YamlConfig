package io.github.pigaut.yaml.amount;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class RandomAmount implements Amount {

    public final double[] values;

    protected RandomAmount(double[] values) {
        this.values = values;
    }

    @Override
    public int intValue() {
        return (int) this.doubleValue();
    }

    @Override
    public double doubleValue() {
        final int randomIndex = ThreadLocalRandom.current().nextInt(0, values.length);
        return values[randomIndex];
    }

    @Override
    public double minValue() {
        double lowest = values[0];
        for (double value : values) {
            if (value < lowest) {
                lowest = value;
            }
        }
        return lowest;
    }

    @Override
    public double maxValue() {
        double highest = values[0];
        for (double value : values) {
            if (value > highest) {
                highest = value;
            }
        }
        return highest;
    }

    @Override
    public boolean match(double amount) {
        for (Double value : values) {
            if (value == amount) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean test(DoublePredicate predicate) {
        for (double value : values) {
            if (!predicate.test(value)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        final double[] mappedValues = Arrays.stream(values)
                .map(mapper::apply)
                .toArray();
        return new RandomAmount(mappedValues);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(";");
        for (double value : values) {
            joiner.add(Double.toString(value));
        }
        return joiner.toString();
    }

}
