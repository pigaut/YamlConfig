package io.github.pigaut.yaml.parser;

import org.jetbrains.annotations.*;

public enum StringStyle implements StringFormatter {

    LOWERCASE("lc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return string.toLowerCase();
        }
    },
    UPPERCASE("uc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return string.toUpperCase();
        }
    },
    TITLE("tc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toTitleCase(string);
        }
    },
    SENTENCE("sc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toSentenceCase(string);
        }
    },
    SPACED("spc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toSpacedCase(string);
        }
    },
    SPACED_UPPERCASE("spu") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toSpacedUpperCase(string);
        }
    },
    SPACED_LOWERCASE("spl") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toSpacedLowerCase(string);
        }
    },
    CONSTANT("cc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toConstantCase(string);
        }
    },
    SNAKE("sn") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toSnakeCase(string);
        }
    },
    KEBAB("kb") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toKebabCase(string);
        }
    },
    PASCAL("ps") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toPascalCase(string);
        }
    },
    CAMEL("cm") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return StringFormatter.toCamelCase(string);
        }
    };

    public static @Nullable StringFormatter getByName(String tagName) {
        for (StringStyle style : values()) {
            if (style.getTagName().equalsIgnoreCase(tagName)) {
                return style;
            }
        }
        return null;
    }

    public static @Nullable StringFormatter getByTag(String tag) {
        for (StringStyle style : values()) {
            if (style.getTag().equalsIgnoreCase(tag)) {
                return style;
            }
        }
        return null;
    }

    public static @NotNull String applyTagStyle(String string) {
        for (StringStyle style : values()) {
            final String tag = style.getTag();
            if (string.contains(tag)) {
                return style.format(string.replace(tag, "").trim());
            }
        }
        return string;
    }

    private final String tagName;

    StringStyle(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTag() {
        return "[" + tagName + "]";
    }

}
