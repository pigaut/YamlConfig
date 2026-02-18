package io.github.pigaut.yaml.amount;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

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

}
