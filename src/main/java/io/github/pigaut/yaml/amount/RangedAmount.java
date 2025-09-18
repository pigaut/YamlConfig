package io.github.pigaut.yaml.amount;

import java.util.concurrent.*;
import java.util.function.*;

public class RangedAmount implements Amount {

    public final double min, max;

    protected RangedAmount(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int getInteger() {
        return (int) this.getDouble();
    }

    @Override
    public double getDouble() {
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

    @Override
    public boolean match(double amount) {
        return amount >= min && amount <= max;
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new RangedAmount(mapper.apply(min), mapper.apply(max));
    }

    @Override
    public String toString() {
        return min + "-" + max;
    }

}
