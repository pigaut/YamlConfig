package io.github.pigaut.yamlib;

import java.util.regex.*;

public class InvalidConfigurationException extends RuntimeException {

    public static boolean DEBUG = true;
    private static final Pattern ALIAS_PATTERN = Pattern.compile("(\\|[a-zA-Z0-9-_]+)");

    public InvalidConfigurationException(ConfigSection section, String message) {
        this(section.getRoot(), section.getPath(), message);
    }

    public InvalidConfigurationException(ConfigSection section, String path, String message) {
        this(section.getRoot(), section.isRoot() ? path : String.join(".", section.getPath(), path), message);
    }

    public InvalidConfigurationException(Config config, String path, String message) {
        this(config.getFile().getPath(), path, message);
    }

    public InvalidConfigurationException(String filePath, String path, String message) {
        super("Error found in \"" + filePath + "\" -> '" + getSimplePath(path) + "' " + message + ".",
                null,
                false,
                DEBUG);
    }

    private static String getSimplePath(String path) {
        Matcher matcher = ALIAS_PATTERN.matcher(path);
        return matcher.replaceAll("");
    }

}
