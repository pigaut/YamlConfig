package io.github.pigaut.yamlib.config;

import io.github.pigaut.yamlib.config.configurator.*;

import java.io.*;

public class SpigotFileConfig extends FileConfig {

    public SpigotFileConfig(File file) {
        super(file, new SpigotConfigurator());
    }

}
