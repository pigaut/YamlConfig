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

    void collectError(@NotNull ConfigException error);
    default void collectError(@NotNull ConfigField field, @NotNull String details) {
        collectError(new InvalidConfigException(field, details));
    }

    void collectWarning(@NotNull ConfigException warning);
    default void collectWarning(@NotNull ConfigField field, @NotNull String details) {
        collectWarning(new InvalidConfigException(field, details));
    }

    void collectAll(@NotNull ErrorCollector other);

    void clearErrors();
    void clearWarnings();
    default void clearErrorsAndWarnings() {
        clearErrors();
        clearWarnings();
    }

}
