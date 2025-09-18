package io.github.pigaut.yaml.amount;

import java.util.function.*;

public class FixedAmount implements Amount {

    public final double value;

    protected FixedAmount(double value) {
        this.value = value;
    }

    @Override
    public int getInteger() {
        return (int) value;
    }

    @Override
    public double getDouble() {
        return value;
    }

    @Override
    public boolean match(double amount) {
        return this.value == amount;
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new FixedAmount(mapper.apply(value));
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

}
