package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

public class ConfigurationLoadException extends RuntimeException {

    private final @Nullable String prefix;
    private final @NotNull String file;
    private final @NotNull String cause;

    public ConfigurationLoadException(ConfigRoot config, String cause) {
        super(null, null, false, config.isDebug());
        this.prefix = config.getPrefix();
        this.file = config.getFile().getPath();
        this.cause = cause;
    }

    public ConfigurationLoadException(ConfigRoot config, Throwable cause) {
        super(null, cause, false, config.isDebug());
        this.prefix = config.getPrefix();
        this.file = config.getFile().getPath();
        this.cause = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        final String optionalPrefix = prefix != null ? (prefix + " ") : "";
        final String errorMessage = "%sConfiguration Loading Error -> Unable to load YAML data\n" +
                "  Problem detected in file: \"%s\".\n" +
                "  Details: %s";
        return String.format(errorMessage, optionalPrefix, file, cause);
    }

}
