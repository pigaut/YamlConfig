package io.github.pigaut.yaml.node;

import org.snakeyaml.engine.v2.api.*;

public class ConfigLoad extends Load {

    public ConfigLoad() {
        super(LoadSettings.builder().build());
    }

}
