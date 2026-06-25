package io.github.pigaut.yaml.delay;

public class TicksUtil {

    public static final int SECOND = 20;
    public static final int MINUTE = 1200;
    public static final int HOUR = 72000;

    public static final int HALF_SECOND = 10;
    public static final int HALF_MINUTE = 600;
    public static final int HALF_HOUR = 36000;

    public static double toHours(int ticks) {
        return (double) (ticks + HALF_HOUR) / HOUR;
    }

    public static double toMinutes(int ticks) {
        return (double) (ticks + HALF_MINUTE) / MINUTE;
    }

    public static double toSeconds(int ticks) {
        return (double) (ticks + HALF_SECOND) / SECOND;
    }

    public static long toMillis(int ticks) {
        return ticks * 50L;
    }

    public static int getHoursPart(int ticks) {
        return ticks / HOUR;
    }

    public static int getMinutesPart(int ticks) {
        return (ticks % HOUR) / MINUTE;
    }

    public static int getSecondsPart(int ticks) {
        // Seconds part rounds to the nearest second
        return ((ticks % MINUTE) + HALF_SECOND) / SECOND;
    }

    public static int count(int hours) {
        return hours * HOUR;
    }

    public static int count(int hours, int minutes) {
        return (hours * HOUR) + (minutes * MINUTE);
    }

    public static int count(int hours, int minutes, int seconds) {
        return (hours * HOUR) + (minutes * MINUTE) + (seconds * SECOND);
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

}
