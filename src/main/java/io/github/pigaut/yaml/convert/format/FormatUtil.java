package io.github.pigaut.yaml.convert.format;

import java.time.*;
import java.time.format.*;
import java.util.*;

public class FormatUtil {

    private FormatUtil() {}

    public static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String formatLocalDate(LocalDate localDate) {
        return DATE_PATTERN.format(localDate);
    }

    public static String formatLocalTime(LocalTime localTime) {
        return TIME_PATTERN.format(localTime);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return DATE_TIME_PATTERN.format(localDateTime);
    }

    public static String formatLocale(Locale locale) {
        return locale.toLanguageTag();
    }

    public static String formatTicks(long ticks) {
        long totalSeconds = ticks / 20;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder builder = new StringBuilder();
        if (hours > 0) builder.append(hours).append("h ");
        if (minutes > 0) builder.append(minutes).append("m ");
        if (seconds > 0 || builder.isEmpty()) builder.append(seconds).append("s");

        return builder.toString().trim();
    }

    public static <T> Formatter<T> defaultFormatter() {
        return Object::toString;
    }

}
