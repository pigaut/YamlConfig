package io.github.pigaut.yaml.amount;

import java.util.function.*;

public abstract class InequalityAmount implements Amount {

    private final double amount;

    public InequalityAmount(double amount) {
        this.amount = amount;
    }

    public static Amount lessThan(double max) {
        return new InequalityAmount(max) {
            @Override
            public boolean match(double amount) {
                return amount < max;
            }
        };
    }

    public static Amount lessThanOrEqualTo(double max) {
        return new InequalityAmount(max) {
            @Override
            public boolean match(double amount) {
                return amount <= max;
            }
        };
    }

    public static Amount greaterThan(double min) {
        return new InequalityAmount(min) {
            @Override
            public boolean match(double amount) {
                return amount > min;
            }
        };
    }

    public static Amount greaterThanOrEqualTo(double min) {
        return new InequalityAmount(min) {
            @Override
            public boolean match(double amount) {
                return amount >= min;
            }
        };
    }

    @Override
    public int getInteger() {
        return (int) amount;
    }

    @Override
    public double getDouble() {
        return amount;
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new MinAmount(mapper.apply(amount));
    }

}
