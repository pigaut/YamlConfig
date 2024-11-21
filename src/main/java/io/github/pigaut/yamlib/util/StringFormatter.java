package io.github.pigaut.yamlib.util;

import org.jetbrains.annotations.*;

public interface StringFormatter {

    @NotNull String format(@NotNull String value);

    static String toSimpleCase(String string) {
        return SIMPLE.format(string);
    }

    static String toConstantCase(String string) {
        return CONSTANT.format(string);
    }

    static String toSnakeCase(String string) {
        return SNAKE.format(string);
    }

    static String toKebabCase(String string) {
        return KEBAB.format(string);
    }

    static String toPascalCase(String string) {
        return PASCAL.format(string);
    }

    static String toCamelCase(String string) {
        return CAMEL.format(string);
    }

    StringFormatter LOWERCASE = String::toLowerCase;

    StringFormatter UPPERCASE = String::toUpperCase;

    StringFormatter SIMPLE = value -> value
            .trim()
            .replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("[-_\\s]+", " ")
            .toLowerCase()
            .trim();

    StringFormatter CONSTANT = value -> value
            .trim()
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .replaceAll("\\s+|-", "_")
            .toUpperCase();

    StringFormatter SNAKE = value -> value
            .trim()
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .replaceAll("\\s+|-", "_")
            .toLowerCase();

    StringFormatter KEBAB = value -> value
            .trim()
            .replaceAll("([a-z])([A-Z])", "$1-$2")
            .replaceAll("\\s+|_", "-")
            .toLowerCase();

    StringFormatter PASCAL = string -> {
        String[] words = string.trim().split("\\s+|-|_|([a-z][A-Z])");
        StringBuilder camelCase = new StringBuilder();


        for (String word : words) {
            if (!word.isEmpty()) {
                camelCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }
        return camelCase.toString();
    };

    StringFormatter CAMEL = string -> {
        String[] words = string.trim().split("\\s+|-|_|([a-z][A-Z])");
        StringBuilder camelCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                camelCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }
        camelCase.replace(0, 1, camelCase.substring(0, 1).toLowerCase());
        return camelCase.toString();
    };

}
