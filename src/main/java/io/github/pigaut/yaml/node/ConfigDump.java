package io.github.pigaut.yaml.node;

import org.snakeyaml.engine.v2.api.*;

public class ConfigDump extends Dump {

    private static final DumpSettings settings;

    static {
        settings = DumpSettings.builder()
                .setIndentWithIndicator(true)
                .setIndicatorIndent(2)
                .setWidth(100)
                .build();
    }

    public ConfigDump() {
        super(settings, new ConfigRepresenter(settings));
    }

}
