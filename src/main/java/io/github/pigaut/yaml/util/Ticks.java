package io.github.pigaut.yaml.util;

public class Ticks {

    public static final int SECOND = 20;
    public static final int MINUTE = 1200;
    public static final int HOUR = 72000;

    public static final int HALF_SECOND = 10;
    public static final int HALF_MINUTE = 600;
    public static final int HALF_HOUR = 36000;

    private final int amount;

    public Ticks(int amount) {
        this.amount = amount;
    }

    public static Ticks of(int hours, int minutes, int seconds) {
        return new Ticks(count(hours, minutes, seconds));
    }

    public static Ticks fromSeconds(int seconds) {
        return new Ticks(seconds * Ticks.SECOND);
    }

    public static Ticks fromMinutes(int minutes) {
        return new Ticks(minutes * Ticks.MINUTE);
    }

    public static Ticks fromHours(int hours) {
        return new Ticks(hours * Ticks.HOUR);
    }

    public static int toHours(int ticks) {
        return (ticks + Ticks.HALF_HOUR) / Ticks.HOUR;
    }

    public static int toMinutes(int ticks) {
        return (ticks + Ticks.HALF_MINUTE) / Ticks.MINUTE;
    }

    public static int toSeconds(int ticks) {
        return (ticks + Ticks.HALF_SECOND) / Ticks.SECOND;
    }

    public static int getHoursPart(int ticks) {
        return ticks / Ticks.HOUR;
    }

    public static int getMinutesPart(int ticks) {
        return (ticks % Ticks.HOUR) / Ticks.MINUTE;
    }

    public static int getSecondsPart(int ticks) {
        // Seconds part rounds to the nearest second
        return ((ticks % Ticks.MINUTE) + Ticks.HALF_SECOND) / Ticks.SECOND;
    }

    public static int count(int hours) {
        return hours * Ticks.HOUR;
    }

    public static int count(int hours, int minutes) {
        return (hours * Ticks.HOUR) + (minutes * Ticks.MINUTE);
    }

    public static int count(int hours, int minutes, int seconds) {
        return (hours * Ticks.HOUR) + (minutes * Ticks.MINUTE) + (seconds * Ticks.SECOND);
    }

    public static String formatCompact(int ticks) {
        int hours = getHoursPart(ticks);
        int minutes = getMinutesPart(ticks);
        int seconds = getSecondsPart(ticks);

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public static String formatFull(int ticks) {
        int hours = getHoursPart(ticks);
        int minutes = getMinutesPart(ticks);
        int seconds = getSecondsPart(ticks);

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
        if (minutes > 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s " : " ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append(" second").append(seconds > 1 ? "s" : "");

        return sb.toString().trim();
    }

    public int getCount() {
        return amount;
    }

    public int toHours() {
        return toHours(amount);
    }

    public int toMinutes() {
        return toMinutes(amount);
    }

    public int toSeconds() {
        return toSeconds(amount);
    }

    @Override
    public String toString() {
        return formatCompact(amount);
    }

}
