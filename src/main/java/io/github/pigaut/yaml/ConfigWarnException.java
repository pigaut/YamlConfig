package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;

public class ConfigWarnException extends InvalidConfigException {

    public ConfigWarnException(@NotNull ConfigField field, @Nullable String details) {
        super(field, details);
    }

    public ConfigWarnException(@NotNull ConfigField field, @NotNull String key, @Nullable String details) {
        super(field, key, details);
    }

    public ConfigWarnException(@NotNull ConfigField field, int index, @Nullable String details) {
        super(field, index, details);
    }

    public ConfigWarnException(@NotNull InvalidConfigException exception, @Nullable String details) {
        super(exception, details);
    }

    @Override
    public boolean isWarning() {
        return true;
    }

}
