package io.github.pigaut.yaml.parser;

import org.jetbrains.annotations.*;

import java.util.*;

public interface StringFormatter {

    @NotNull String format(@NotNull String string);

    static String toTitleCase(String string) {
        if (string.isBlank()) {
            return string;
        }
        return toTitleCase(splitWords(string));
    }

    static String toTitleCase(Class<?> clazz) {
        return toTitleCase(splitClassName(clazz));
    }

    static String toTitleCase(String... words) {
        StringJoiner result = new StringJoiner(" ");
        for (String word : words) {
            if (!word.isBlank()) {
                result.add(Character.toUpperCase(word.charAt(0)) +
                        (word.length() > 1 ? word.substring(1).toLowerCase() : ""));
            }
        }
        return result.toString();
    }

    static String toSentenceCase(String string) {
        return toSentenceCase(splitWords(string));
    }

    static String toSentenceCase(Class<?> clazz) {
        return toSentenceCase(splitClassName(clazz));
    }

    static String toSentenceCase(String... words) {
        final String string = String.join(" ", words).toLowerCase();
        if (string.isBlank()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + (string.length() > 1 ? string.substring(1) : "");
    }

    static String toSpacedCase(String string) {
        return String.join(" ", splitWords(string));
    }

    static String toSpacedLowerCase(String string) {
        return toSpacedCase(string).toLowerCase();
    }

    static String toSpacedUpperCase(String string) {
        return toSpacedCase(string).toUpperCase();
    }

    static String toConstantCase(String string) {
        return String.join("_", splitWords(string)).toUpperCase();
    }

    static String toSnakeCase(String string) {
        return String.join("_", splitWords(string)).toLowerCase();
    }

    static String toKebabCase(String string) {
        return String.join("-", splitWords(string)).toLowerCase();
    }

    static String toPascalCase(String string) {
        String[] words = splitWords(string);
        StringBuilder camelCase = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                camelCase.append(Character.toUpperCase(word.charAt(0)));
                camelCase.append(word.length() > 1 ? word.substring(1).toLowerCase() : "");
            }
        }
        return camelCase.toString();
    }

    static String toCamelCase(String string) {
        String[] words = splitWords(string);
        StringBuilder camelCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                camelCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }
        if (!camelCase.isEmpty()) {
            camelCase.replace(0, 1, camelCase.substring(0, 1).toLowerCase());
        }
        return camelCase.toString();
    }

    static boolean match(@NotNull String key, @Nullable String keyToCompare) {
        if (keyToCompare == null) {
            return false;
        }
        return toConstantCase(key).equals(toConstantCase(keyToCompare));
    }

    static boolean containsDelimiter(String string) {
        return string.contains("-") || string.contains("_") || string.contains(" ");
    }

    static String[] splitClassName(Class<?> clazz) {
        return clazz.getSimpleName().trim().replaceAll("([a-z])([A-Z])", "$1 $2").split(" ");
    }

    static String[] splitWords(String string) {
        string = string.trim();
        if (!containsDelimiter(string)) {
            string = string.replaceAll("([a-z])([A-Z])", "$1 $2");
        }
        return string.split("[-_\\s]+");
    }

}
