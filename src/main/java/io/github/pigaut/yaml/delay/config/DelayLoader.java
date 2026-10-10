package io.github.pigaut.yaml.delay.config;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.delay.*;
import org.jetbrains.annotations.*;

public class DelayLoader implements ConfigLoader<Delay> {

    @Override
    public @Nullable String getErrorDescription() {
        return "invalid delay";
    }

    @Override
    public @NotNull Delay loadFromScalar(@NotNull ConfigScalar scalar) throws InvalidConfigException {
        try {
            return ParseUtil.parseDelay(scalar.toString());
        } catch (StringParseException e) {
            throw new ConfigErrorException(scalar, e.getMessage());
        }
    }

}
