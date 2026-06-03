package io.github.pigaut.yaml.delay;

public interface Delay {

    static Delay of(int hours, int minutes, int seconds) {
        return new FixedDelay(TicksUtil.count(hours, minutes, seconds));
    }

    static Delay fromTicks(int ticks) {
        return new FixedDelay(ticks);
    }

    static Delay fromSeconds(int seconds) {
        return new FixedDelay(TicksUtil.count(0, 0, seconds));
    }

    static Delay fromMinutes(int minutes) {
        return new FixedDelay(TicksUtil.count(0, minutes));
    }

    static Delay fromHours(int hours) {
        return new FixedDelay(TicksUtil.count(hours));
    }

    static Delay between(int minTicks, int maxTicks) {
        return new RangedDelay(minTicks, maxTicks);
    }

    int toTicks();

    int toHours();

    int toMinutes();

    int toSeconds();

}
