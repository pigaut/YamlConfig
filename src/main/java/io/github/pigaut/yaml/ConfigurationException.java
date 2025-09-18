package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;

public abstract class ConfigurationException extends RuntimeException {

    protected ConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public @NotNull String getLogMessage() {
        return getLogMessage("");
    }

    public abstract @NotNull String getLogMessage(String parentDirectory);

}
