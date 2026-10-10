package io.github.pigaut.yaml.convert.format;

import org.jetbrains.annotations.*;

public class NumberFormatter {

    private NumberFormatter() {
    }

    public static String toDecimals0(Number number) {
        return NumberStyle.DECIMALS_0.format(number);
    }

    public static String toDecimals1(Number number) {
        return NumberStyle.DECIMALS_1.format(number);
    }

    public static String toDecimals2(Number number) {
        return NumberStyle.DECIMALS_2.format(number);
    }

    public static String toDecimals3(Number number) {
        return NumberStyle.DECIMALS_3.format(number);
    }

    public static String toRoman(Number number) {
        return NumberStyle.ROMAN.format(number);
    }

    public static String toThousands(Number number) {
        return NumberStyle.THOUSANDS.format(number);
    }

    public static String toCommas(Number number) {
        return NumberStyle.COMMAS.format(number);
    }

    public static String toOrdinal(Number number) {
        return NumberStyle.ORDINAL.format(number);
    }

    public static String toCompact(Number number) {
        return NumberStyle.COMPACT.format(number);
    }

    /** Formats with the given style, or as a plain number if the style is null. */
    public static String format(Number number, @Nullable NumberStyle style) {
        return style == null ? toPlain(number) : style.format(number);
    }

    /** Formats using a style's short name (e.g. "k", "d2", "ord"). Unknown names fall back to plain. */
    public static String format(Number number, @Nullable String styleName) {
        return format(number, styleName == null ? null : NumberStyle.getByName(styleName.trim()));
    }

    /** Whole values drop the decimal part (5.0 -> "5"). */
    public static String toPlain(Number number) {
        double d = number.doubleValue();
        if (d == (long) d) {
            return String.valueOf((long) d);
        }
        return String.valueOf(number);
    }

}