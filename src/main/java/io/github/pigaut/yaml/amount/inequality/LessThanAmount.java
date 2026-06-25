package io.github.pigaut.yaml.amount.inequality;

import io.github.pigaut.yaml.amount.*;

import java.util.function.*;

public class LessThanAmount implements Amount {

    private final double max;

    public LessThanAmount(double max) {
        this.max = max;
    }

    @Override
    public int intValue() {
        return (int) max;
    }

    @Override
    public double doubleValue() {
        return max;
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return max;
    }

    @Override
    public boolean match(double amount) {
        return amount < max;
    }

    @Override
    public boolean test(DoublePredicate predicate) {
        return predicate.test(max);
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new LessThanAmount(mapper.apply(max));
    }

    @Override
    public String toString() {
        return "<" + max;
    }

}
