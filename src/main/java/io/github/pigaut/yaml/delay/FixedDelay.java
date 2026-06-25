package io.github.pigaut.yaml.delay;

public class FixedDelay implements Delay {

    private final int ticks;

    public FixedDelay(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public int toTicks() {
        return ticks;
    }

    @Override
    public double toHours() {
        return TicksUtil.toHours(ticks);
    }

    @Override
    public double toMinutes() {
        return TicksUtil.toMinutes(ticks);
    }

    @Override
    public double toSeconds() {
        return TicksUtil.toSeconds(ticks);
    }

    @Override
    public long toMillis() {
        return TicksUtil.toMillis(ticks);
    }

    @Override
    public String toString() {
        return TicksUtil.formatCompact(ticks);
    }

}
