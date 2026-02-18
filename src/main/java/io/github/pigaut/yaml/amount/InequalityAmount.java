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

            @Override
            public boolean test(DoublePredicate predicate) {
                return predicate.test(max);
            }
        };
    }

    public static Amount lessThanOrEqualTo(double max) {
        return new InequalityAmount(max) {
            @Override
            public boolean match(double amount) {
                return amount <= max;
            }

            @Override
            public boolean test(DoublePredicate predicate) {
                return predicate.test(max);
            }
        };
    }

    public static Amount greaterThan(double min) {
        return new InequalityAmount(min) {
            @Override
            public boolean match(double amount) {
                return amount > min;
            }
            @Override
            public boolean test(DoublePredicate predicate) {
                return predicate.test(min);
            }
        };
    }

    public static Amount greaterThanOrEqualTo(double min) {
        return new InequalityAmount(min) {
            @Override
            public boolean match(double amount) {
                return amount >= min;
            }
            @Override
            public boolean test(DoublePredicate predicate) {
                return predicate.test(min);
            }
        };
    }

    @Override
    public int intValue() {
        return (int) amount;
    }

    @Override
    public double doubleValue() {
        return amount;
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return new MinAmount(mapper.apply(amount));
    }

}
