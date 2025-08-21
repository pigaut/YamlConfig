package io.github.pigaut.yaml.convert.format;

import org.jetbrains.annotations.*;

public enum CaseStyle implements StringFormatter {

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
            return CaseFormatter.toTitleCase(string);
        }
    },
    SENTENCE("sc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toSentenceCase(string);
        }
    },
    SPACED("spc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toSpacedCase(string);
        }
    },
    SPACED_UPPERCASE("spu") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toSpacedUpperCase(string);
        }
    },
    SPACED_LOWERCASE("spl") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toSpacedLowerCase(string);
        }
    },
    CONSTANT("cc") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toConstantCase(string);
        }
    },
    SNAKE("sn") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toSnakeCase(string);
        }
    },
    KEBAB("kb") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toKebabCase(string);
        }
    },
    PASCAL("ps") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toPascalCase(string);
        }
    },
    CAMEL("cm") {
        @Override
        public @NotNull String format(@NotNull String string) {
            return CaseFormatter.toCamelCase(string);
        }
    };

    public static @Nullable StringFormatter getByName(String tagName) {
        for (CaseStyle style : values()) {
            if (style.getTagName().equalsIgnoreCase(tagName)) {
                return style;
            }
        }
        return null;
    }

    public static @Nullable StringFormatter getByTag(String tag) {
        for (CaseStyle style : values()) {
            if (style.getTag().equalsIgnoreCase(tag)) {
                return style;
            }
        }
        return null;
    }

    public static @NotNull String translateTagStyle(String string) {
        for (CaseStyle style : values()) {
            final String tag = style.getTag();
            if (string.contains(tag)) {
                return style.format(string.replace(tag, "").trim());
            }
        }
        return string;
    }

    private final String tagName;

    CaseStyle(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTag() {
        return "[" + tagName + "]";
    }

}
