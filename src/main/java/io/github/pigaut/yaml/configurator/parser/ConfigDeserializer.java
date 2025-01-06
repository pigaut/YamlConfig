package io.github.pigaut.yaml.configurator.parser;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.loader.*;
import io.github.pigaut.yaml.parser.*;
import io.github.pigaut.yaml.parser.deserializer.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ConfigDeserializer<T> extends ScalarLoader<T>, Deserializer<T> {

    @Override
    default @NotNull T loadFromScalar(ConfigScalar scalar) throws InvalidConfigurationException {
        try {
            return deserialize(scalar.toString());
        } catch (DeserializationException e) {
            throw new InvalidConfigurationException(scalar, e.getMessage());
        }
    }

}
