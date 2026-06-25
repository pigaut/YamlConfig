package io.github.pigaut.yaml.convert.format;

import org.jetbrains.annotations.*;

import java.text.*;
import java.util.*;

public enum NumberStyle {

    PERCENT_2("%2") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return formatPercent(number, 2);
        }
    },

    PERCENT_1("%1") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return formatPercent(number, 1);
        }
    },

    PERCENT("%") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return formatPercent(number, 0);
        }
    },

    ROMAN("rm") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            double d = number.doubleValue();
            if (d == (int) d) {
                int value = (int) d;
                if (value >= 1 && value <= 30) {
                    return ROMAN_NUMERALS[value];
                }
            }
            return sanitize(number);
        }
    },

    THOUSANDS("k") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            double d = number.doubleValue();
            if (d == (long) d) {
                long value = (long) d;
                if (value != 0 && value % 1000 == 0) {
                    return (value / 1000) + "k";
                }
            }
            return sanitize(number);
        }
    },

    COMMAS("cm") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return NumberFormat.getInstance(Locale.US).format(number);
        }
    },

    ORDINAL("ord") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            double d = number.doubleValue();
            if (d == (int) d && d > 0) {
                int value = (int) d;
                int remainder10 = value % 10;
                int remainder100 = value % 100;

                if (remainder10 == 1 && remainder100 != 11) return value + "st";
                if (remainder10 == 2 && remainder100 != 12) return value + "nd";
                if (remainder10 == 3 && remainder100 != 13) return value + "rd";
                return value + "th";
            }
            return sanitize(number);
        }
    },

    COMPACT("cp") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            double value = number.doubleValue();
            if (value >= 1_000_000) {
                return String.format(Locale.US, "%.1fm", value / 1_000_000).replace(".0", "");
            }
            if (value >= 1_000) {
                return String.format(Locale.US, "%.1fk", value / 1_000).replace(".0", "");
            }
            return sanitize(number);
        }
    };

    private final String tagName;

    private static final String[] ROMAN_NUMERALS = {
            "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
            "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX",
            "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII", "XXIX", "XXX"
    };

    NumberStyle(String tagName) {
        this.tagName = tagName;
    }

    public abstract @NotNull String format(@NotNull Number number);

    public String getTagName() {
        return this.tagName;
    }

    public String getTag() {
        return "[" + this.tagName + "]";
    }

    /**
     * Helper to cleanly convert a Number to a String fallback.
     * Prevents integers passed as doubles from showing trailing zeros (e.g., 5.0 -> 5)
     */
    private static String sanitize(Number number) {
        double d = number.doubleValue();
        if (d == (long) d) {
            return String.valueOf((long) d);
        }
        return String.valueOf(number);
    }

    public static @Nullable NumberStyle getByName(String tagName) {
        for (NumberStyle style : values()) {
            if (style.getTagName().equalsIgnoreCase(tagName)) {
                return style;
            }
        }
        return null;
    }

    public static @Nullable NumberStyle getByTag(String tag) {
        for (NumberStyle style : values()) {
            if (style.getTag().equalsIgnoreCase(tag)) {
                return style;
            }
        }
        return null;
    }

    /**
     * Parses the tag, extracts the numeric value safely, and applies the format.
     */
    public static @NotNull String translateTagStyle(String string) {
        for (NumberStyle style : values()) {
            String tag = style.getTag();
            if (string.contains(tag)) {
                String numericStr = string.replace(tag, "").trim();
                try {
                    // Dynamically parse to Double or Long based on decimal presence
                    Number number = numericStr.contains(".")
                            ? Double.parseDouble(numericStr)
                            : Long.parseLong(numericStr);

                    return style.format(number);
                } catch (NumberFormatException e) {
                    return string; // Fallback to raw string if parsing fails
                }
            }
        }
        return string;
    }

    private static @NotNull String formatPercent(@NotNull Number number, int decimals) {
        double percent = number.doubleValue() * 100d;
        if (decimals <= 0) {
            return String.valueOf(Math.round(percent));
        }
        String pattern = "%." + decimals + "f";
        return String.format(Locale.US, pattern, percent);
    }

}