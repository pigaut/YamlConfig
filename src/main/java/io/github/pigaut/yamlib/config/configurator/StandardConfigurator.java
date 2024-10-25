package io.github.pigaut.yamlib.config.configurator;

import java.util.*;

public class StandardConfigurator extends Configurator {

    public StandardConfigurator() {
        registerMapper(Map.class, ConfigMapper.MAP);
        registerMapper(Iterable.class, ConfigMapper.ITERABLE);
    }

}
