package io.github.pigaut.yaml.configurator.convert.deserialize;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.convert.parse.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Deserializer<T> extends ConfigLoader.Scalar<T> {

    T deserialize(String string) throws StringParseException;

    @Override
    default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigException {
        try {
            return deserialize(scalar.toString());
        } catch (StringParseException e) {
            throw new InvalidConfigException(scalar, e.getMessage());
        }
    }

    default @NotNull T loadFromKey(ConfigSection section, String key) throws InvalidConfigException {
        try {
            return deserialize(key);
        } catch (StringParseException e) {
            throw new InvalidConfigException(section, e.getMessage());
        }
    }

}
