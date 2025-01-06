package io.github.pigaut.yaml;

import io.github.pigaut.yaml.parser.*;
import org.bukkit.*;

import java.util.regex.*;

public class SpigotYamlConfig {

    private SpigotYamlConfig() {}

    public static final String formatString(String string) {
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

        string = ChatColor.translateAlternateColorCodes('&', string);
        string = StringStyle.applyTagStyle(string);
        return string;
    }

    public static final StringFormatter COLOR_FORMATTER = string -> {
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
        return ChatColor.translateAlternateColorCodes('&', string);
    };


}
