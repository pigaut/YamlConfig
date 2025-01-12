package io.github.pigaut.yaml.formatter;

import io.github.pigaut.yaml.parser.*;
import org.bukkit.*;

import java.util.regex.*;

public class StringColor {

    public static final StringFormatter FORMATTER = StringColor::translateColors;

    public static String translateColorsAndStyle(String string) {
        return StringColor.translateColors(StringStyle.translateTagStyle(string));
    }

    public static String translateColors(String string) {
        return translateHexColors(translateColorCodes(string));
    }

    public static String translateColorCodes(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String translateHexColors(String string) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String hexCode = string.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&" + c);
            }

            string = string.replace(hexCode, builder.toString());
            matcher = pattern.matcher(string);
        }
        return string;
    }

}
