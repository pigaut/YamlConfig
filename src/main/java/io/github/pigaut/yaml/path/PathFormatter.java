package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.convert.format.*;

import java.util.regex.*;

public class PathFormatter {

    public static final Pattern ALIAS_PATTERN = Pattern.compile("(\\|[a-zA-Z0-9-_]+)");
    public static final Pattern INDEX_PATTERN = Pattern.compile("\\[(\\d+)]");

    public static final StringFormatter NO_ALIASES = path -> ALIAS_PATTERN.matcher(path).replaceAll("");
    public static final StringFormatter NO_INDICES = path -> INDEX_PATTERN.matcher(path).replaceAll(".$1");
    public static final StringFormatter RAW = path -> NO_INDICES.format(NO_ALIASES.format(path));

    public static String getPathWithoutAliases(String path) {
        return NO_ALIASES.format(path);
    }

    public static String getPathWithoutIndices(String path) {
        return NO_INDICES.format(path);
    }

    public static String getRawPath(String path) {
        return RAW.format(path);
    }

    public static String[] getRawKeys(String path) {
        return getRawPath(path).split("\\.");
    }

    public static String getLastKey(String path) {
        String[] keys = getRawKeys(path);
        return keys[keys.length - 1];
    }

}
