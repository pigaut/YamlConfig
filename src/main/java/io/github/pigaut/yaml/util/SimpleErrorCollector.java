package io.github.pigaut.yaml.util;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class SimpleErrorCollector implements ErrorCollector {

    private final List<ConfigException> errors = new ArrayList<>();
    private final List<ConfigException> warnings = new ArrayList<>();

    @Override
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    @Override
    public int getErrorCount() {
        return errors.size();
    }

    @Override
    public int getWarningCount() {
        return warnings.size();
    }

    @Override
    public @NotNull List<ConfigException> getErrors() {
        return new ArrayList<>(errors);
    }

    @Override
    public @NotNull List<ConfigException> getWarnings() {
        return new ArrayList<>(warnings);
    }

    @Override
    public void collectError(@NotNull ConfigException error) {
        errors.add(error);
    }

    @Override
    public void collectWarning(@NotNull ConfigException warning) {
        warnings.add(warning);
    }

    @Override
    public void collectAll(@NotNull ErrorCollector other) {
        for (ConfigException error : other.getErrors()) {
            collectError(error);
        }
        for (ConfigException warning : other.getWarnings()) {
            collectWarning(warning);
        }
    }

    @Override
    public void clearErrors() {
        errors.clear();
    }

    @Override
    public void clearWarnings() {
        warnings.clear();
    }

}
