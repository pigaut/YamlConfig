package io.github.pigaut.yaml.amount;

import java.util.function.*;

public class MinAmount implements Amount {

    private final double min;

    public MinAmount(double min) {
        this.min = min;
    }

    @Override
    public int getInteger() {
        return (int) min;
    }

    @Override
    public double getDouble() {
        return min;
    }

    @Override
    public boolean match(double amount) {
        return min >= amount;
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new MinAmount(mapper.apply(min));
    }

}
