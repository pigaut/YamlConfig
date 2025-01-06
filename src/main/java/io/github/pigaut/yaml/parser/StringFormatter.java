package io.github.pigaut.yaml.parser;

import org.jetbrains.annotations.*;

import java.util.*;

public interface StringFormatter {

    @NotNull String format(@NotNull String value);

    static String toTitleCase(String string) {
        return TITLE_CASE.format(string);
    }

    static String toSentenceCase(String string) {
        return SENTENCE_CASE.format(string);
    }

    static String toSpacedCase(String string) {
        return SPACED_CASE.format(string);
    }

    static String toSpacedLowerCase(String string) {
        return SPACED_CASE.format(string).toLowerCase();
    }

    static String toSpacedUpperCase(String string) {
        return SPACED_CASE.format(string).toUpperCase();
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

    static boolean match(@NotNull String key, @Nullable String keyToCompare) {
        if (keyToCompare == null) {
            return false;
        }
        return toConstantCase(key).equals(toConstantCase(keyToCompare));
    }

    static String applyTagFormat(String string) {
        for (String tag : FORMATTERS_BY_TAG.keySet()) {
            if (string.contains(tag)) {
                return getByTag(tag).format(string.replace(tag, "").trim());
            }
        }
        return string;
    }

    static @Nullable StringFormatter getByName(String name) {
        return FORMATTERS_BY_NAME.get(name);
    }

    static Collection<String> getFormatNames() {
        return new ArrayList<>(FORMATTERS_BY_NAME.keySet());
    }

    static Collection<String> getFormatTags() {
        return new ArrayList<>(FORMATTERS_BY_TAG.keySet());
    }

    static @Nullable StringFormatter getByTag(String tag) {
        return FORMATTERS_BY_TAG.get(tag);
    }

    StringFormatter LOWERCASE = String::toLowerCase;

    StringFormatter UPPERCASE = String::toUpperCase;

    StringFormatter SENTENCE_CASE = value -> {
        value = toSpacedCase(value);
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    };

    StringFormatter TITLE_CASE = string -> {
        String[] words = string.trim().split("\\s+|-|_|([a-z][A-Z])");
        StringBuilder camelCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                camelCase.append(" ")
                        .append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }
        return camelCase.toString();
    };

    StringFormatter SPACED_CASE = value -> value
            .trim()
            .replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("[-_\\s]+", " ")
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

    Map<String, StringFormatter> FORMATTERS_BY_NAME = Map.of(
            "title_case", TITLE_CASE,
            "sentence_case", SENTENCE_CASE,
            "spaced_case", SPACED_CASE,
            "constant_case", CONSTANT,
            "snake_case", SNAKE,
            "kebab_case", KEBAB,
            "pascal_case", PASCAL,
            "camel_case", CAMEL
    );

    Map<String, StringFormatter> FORMATTERS_BY_TAG = Map.of(
            "tc", TITLE_CASE,
            "sc", SENTENCE_CASE,
            "sp", SPACED_CASE,
            "spl", SPACED_CASE,
            "spu", SPACED_CASE,
            "cc", CONSTANT,
            "sn", SNAKE,
            "kb", KEBAB,
            "pc", PASCAL,
            "cm", CAMEL
    );

}
