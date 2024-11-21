package io.github.pigaut.yamlib;

import io.github.pigaut.yamlib.util.*;

public class InvalidConfigurationException extends RuntimeException {

    public static boolean DEBUG = true;

    public InvalidConfigurationException(ConfigSection section, String message) {
        this(section.getRoot(), section.getPath(), message);
    }

    public InvalidConfigurationException(ConfigSection section, String key, String message) {
        this(section.getRoot(), section.isRoot() ? key : String.join(".", section.getPath(), key), message);
    }

    public InvalidConfigurationException(Config config, String path, String message) {
        this(config.getFile().getPath(), path, message);
    }

    public InvalidConfigurationException(String filePath, String path, String message) {
        super("Error found in \"" + filePath + "\" -> '" + PathFormatter.getPathWithoutAliases(path) + "' " + message + ".",
                null,
                false,
                DEBUG);
    }

}
