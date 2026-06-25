package io.github.pigaut.yaml.delay;

import io.github.pigaut.yaml.util.*;

import java.util.concurrent.*;

public class RangedDelay implements Delay {

    private final int minTicks;
    private final int maxTicks;

    public RangedDelay(int minTicks, int maxTicks) {
        Preconditions.checkArgument(minTicks < maxTicks, "Min ticks must be less than max ticks.");
        this.minTicks = minTicks;
        this.maxTicks = maxTicks;
    }

    @Override
    public int toTicks() {
        return ThreadLocalRandom.current().nextInt(minTicks, maxTicks + 1);
    }

    @Override
    public double toHours() {
        return TicksUtil.toHours(toTicks());
    }

    @Override
    public double toMinutes() {
        return TicksUtil.toMinutes(toTicks());
    }

    @Override
    public double toSeconds() {
        return TicksUtil.toSeconds(toTicks());
    }

    @Override
    public long toMillis() {
        return TicksUtil.toMillis(toTicks());
    }

    @Override
    public String toString() {
        double minSeconds = TicksUtil.toSeconds(minTicks);
        double maxSeconds = TicksUtil.toSeconds(maxTicks);
        return NumberUtil.formatDouble(minSeconds) + "-" + NumberUtil.formatDouble(maxSeconds) + "s";
    }

}
