package io.github.pigaut.yaml.configurator.convert;

import io.github.pigaut.yaml.configurator.convert.deserialize.*;
import io.github.pigaut.yaml.configurator.convert.serialize.*;

public interface Converter<T> extends Deserializer<T>, Serializer<T> {

}
