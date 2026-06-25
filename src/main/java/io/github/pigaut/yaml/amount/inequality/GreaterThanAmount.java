package io.github.pigaut.yaml.amount.inequality;

import io.github.pigaut.yaml.amount.*;

import java.util.function.*;

public class GreaterThanAmount implements Amount {

    private final double min;

    public GreaterThanAmount(double min) {
        this.min = min;
    }

    @Override
    public int intValue() {
        return (int) min;
    }

    @Override
    public double doubleValue() {
        return min;
    }

    @Override
    public double minValue() {
        return min;
    }

    @Override
    public double maxValue() {
        return Double.MAX_VALUE;
    }

    @Override
    public boolean match(double amount) {
        return amount > min;
    }

    @Override
    public boolean test(DoublePredicate predicate) {
        return predicate.test(min);
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new GreaterThanAmount(mapper.apply(min));
    }

    @Override
    public String toString() {
        return ">" + min;
    }

}
