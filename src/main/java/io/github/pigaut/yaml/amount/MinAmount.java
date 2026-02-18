package io.github.pigaut.yaml.amount;

import java.util.function.*;

public class MinAmount implements Amount {

    private final double min;

    public MinAmount(double min) {
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
    public boolean match(double amount) {
        return min >= amount;
    }

    @Override
    public boolean test(DoublePredicate predicate) {
        return predicate.test(min);
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new MinAmount(mapper.apply(min));
    }

}
