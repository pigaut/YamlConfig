package io.github.pigaut.yaml.amount;

import io.github.pigaut.yaml.chance.*;
import org.jetbrains.annotations.*;

import java.util.function.*;

public class ChanceAmount implements Amount {

    private final Amount amount;
    private final Chance chance;

    public ChanceAmount(@NotNull Amount amount, @NotNull Chance chance) {
        this.amount = amount;
        this.chance = chance;
    }

    @Override
    public int intValue() {
        return chance.test() ? amount.intValue() : 0;
    }

    @Override
    public double doubleValue() {
        return chance.test() ? amount.doubleValue() : 0;
    }

    @Override
    public double minValue() {
        return amount.minValue();
    }

    @Override
    public double maxValue() {
        return amount.maxValue();
    }

    @Override
    public boolean match(double amount) {
        return this.amount.match(amount);
    }

    @Override
    public boolean test(DoublePredicate predicate) {
        return amount.test(predicate);
    }

    @Override
    public Amount transform(DoubleFunction<Double> mapper) {
        return amount.transform(mapper);
    }
}
