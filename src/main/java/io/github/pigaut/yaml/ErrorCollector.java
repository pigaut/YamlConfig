package io.github.pigaut.yaml;

import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface ErrorCollector {

    ErrorCollector EMPTY = new EmptyErrorCollector();

    boolean hasErrors();
    boolean hasWarnings();

    int getErrorCount();
    int getWarningCount();

    @NotNull List<ConfigException> getErrors();
    @NotNull List<ConfigException> getWarnings();

    default void collect(@NotNull ConfigException exception) {
        if (exception.isWarning()) {
            collectWarning(exception);
            return;
        }
        collectError(exception);
    }

    void collectError(@NotNull ConfigException error);

    default void collectError(@NotNull ConfigField field, @NotNull String details) {
        collectError(new ConfigErrorException(field, details));
    }
    default void collectError(@NotNull ConfigField field, @NotNull String key, @NotNull String details) {
        collectError(new ConfigErrorException(field, key, details));
    }
    default void collectError(@NotNull ConfigField field, int index, @NotNull String details) {
        collectError(new ConfigErrorException(field, index, details));
    }

    void collectWarning(@NotNull ConfigException warning);

    default void collectWarning(@NotNull ConfigField field, @NotNull String details) {
        collectWarning(new ConfigWarnException(field, details));
    }
    default void collectWarning(@NotNull ConfigField field, int index, @NotNull String details) {
        collectWarning(new ConfigWarnException(field, index, details));
    }

    void collectAll(@NotNull ErrorCollector other);

    void clearErrors();
    void clearWarnings();
    default void clearErrorsAndWarnings() {
        clearErrors();
        clearWarnings();
    }

}
