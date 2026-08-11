package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class EmptyErrorCollector implements ErrorCollector {

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public boolean hasWarnings() {
        return false;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    @Override
    public int getWarningCount() {
        return 0;
    }

    @Override
    public @NotNull List<ConfigException> getErrors() {
        return List.of();
    }

    @Override
    public @NotNull List<ConfigException> getWarnings() {
        return List.of();
    }

    @Override
    public void collectError(@NotNull ConfigException error) {

    }

    @Override
    public void collectWarning(@NotNull ConfigException warning) {

    }

    @Override
    public void collectAll(@NotNull ErrorCollector other) {

    }

    @Override
    public void clearErrors() {

    }

    @Override
    public void clearWarnings() {

    }
}
