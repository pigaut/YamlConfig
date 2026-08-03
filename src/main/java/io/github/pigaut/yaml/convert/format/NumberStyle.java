package io.github.pigaut.yaml.convert.format;

import org.jetbrains.annotations.*;

import java.text.*;
import java.util.*;

public enum NumberStyle {

    DECIMALS_0("d0") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return formatDecimals(number, 0);
        }
    },

    DECIMALS_1("d1") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return formatDecimals(number, 1);
        }
    },

    DECIMALS_2("d2") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return formatDecimals(number, 2);
        }
    },

    DECIMALS_3("d3") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            return formatDecimals(number, 3);
        }
    },

    ROMAN("rm") {
        @Override
        public @NotNull String format(@NotNull Number number) {
            double d = number.doubleValue();
            if (d == (int) d) {
                int value = (int) d;
                if (value >= 0 && value < ROMAN_NUMERALS.length) {
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
            "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII", "XXIX", "XXX",
            "XXXI", "XXXII", "XXXIII", "XXXIV", "XXXV", "XXXVI", "XXXVII", "XXXVIII", "XXXIX", "XL",
            "XLI", "XLII", "XLIII", "XLIV", "XLV", "XLVI", "XLVII", "XLVIII", "XLIX", "L",
            "LI", "LII", "LIII", "LIV", "LV", "LVI", "LVII", "LVIII", "LIX", "LX",
            "LXI", "LXII", "LXIII", "LXIV", "LXV", "LXVI", "LXVII", "LXVIII", "LXIX", "LXX",
            "LXXI", "LXXII", "LXXIII", "LXXIV", "LXXV", "LXXVI", "LXXVII", "LXXVIII", "LXXIX", "LXXX",
            "LXXXI", "LXXXII", "LXXXIII", "LXXXIV", "LXXXV", "LXXXVI", "LXXXVII", "LXXXVIII", "LXXXIX", "XC",
            "XCI", "XCII", "XCIII", "XCIV", "XCV", "XCVI", "XCVII", "XCVIII", "XCIX", "C"
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

    private static @NotNull String formatDecimals(@NotNull Number number, int decimals) {
        double value = number.doubleValue();
        if (decimals <= 0) {
            return String.valueOf(Math.round(value));
        }
        String pattern = "%." + decimals + "f";
        return String.format(Locale.US, pattern, value);
    }

}