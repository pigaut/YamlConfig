package io.github.pigaut.yaml.chance;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.convert.parse.*;
import org.jetbrains.annotations.*;

public class ChanceLoader implements ConfigLoader<Chance> {

    @Override
    public @Nullable String getErrorDescription() {
        return "invalid chance";
    }

    @Override
    public @NotNull Chance loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
        try {
            return ParseUtil.parseChance(scalar.toString());
        } catch (StringParseException e) {
            throw new InvalidConfigException(scalar, e.getMessage());
        }
    }

}
