package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;

public class ConfigErrorException extends InvalidConfigException {

    public ConfigErrorException(@NotNull ConfigField field, @Nullable String details) {
        super(field, details);
    }

    public ConfigErrorException(@NotNull ConfigField field, @NotNull String key, @Nullable String details) {
        super(field, key, details);
    }

    public ConfigErrorException(@NotNull ConfigField field, int index, @Nullable String details) {
        super(field, index, details);
    }

    public ConfigErrorException(@NotNull InvalidConfigException exception, @Nullable String details) {
        super(exception, details);
    }

    @Override
    public boolean isWarning() {
        return false;
    }
}
