package io.github.pigaut.yaml.amount;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class CasualAmount implements Amount {

    public final double[] values;

    protected CasualAmount(double[] values) {
        this.values = values;
    }

    @Override
    public int getInteger() {
        return (int) this.getDouble();
    }

    @Override
    public double getDouble() {
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
    public Amount transform(DoubleFunction<Double> mapper) {
        final double[] mappedValues = Arrays.stream(values)
                .map(mapper::apply)
                .toArray();
        return new CasualAmount(mappedValues);
    }

}
