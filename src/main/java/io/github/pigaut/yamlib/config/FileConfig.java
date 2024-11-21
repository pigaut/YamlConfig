package io.github.pigaut.yamlib.config;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.config.node.*;
import io.github.pigaut.yamlib.configurator.*;
import io.github.pigaut.yamlib.parser.*;
import io.github.pigaut.yamlib.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;

import java.io.*;

public class FileConfig extends RootSection implements Config {

    private final File file;
    private final String name;
    private Configurator configurator;

    public FileConfig(File file) {
        this(file, new StandardConfigurator());
    }

    public FileConfig(File file, Configurator configurator) {
        Preconditions.checkNotNull(file, "File cannot be null");
        Preconditions.checkNotNull(configurator, "Configurator cannot be null");
        this.file = file;
        this.name = YAMLib.getFileName(file.getName());
        this.configurator = configurator;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull File getFile() {
        return file;
    }

    @Override
    public @NotNull Configurator getConfigurator() {
        return configurator;
    }

    @Override
    public void setConfigurator(@NotNull Configurator configurator) {
        this.configurator = configurator;
    }

    @Override
    public boolean save() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }

        DumpSettings settings = DumpSettings.builder()
                .setIndentWithIndicator(true)
                .setIndicatorIndent(2)
                .build();

        Dump dumper = new Dump(settings, new ConfigRepresenter(settings));
        String yaml = dumper.dumpToString(this);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(yaml);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean load() {
        this.clear();

        if (!file.exists()) {
            return false;
        }

        LoadSettings settings = LoadSettings.builder().build();
        Load loader = new Load(settings);

        Object data;
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            data = loader.loadFromReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (data != null) {
            configurator.map(this, data);
        }
        return true;
    }

    @Override
    public void remove() {
        this.clear();
        file.delete();
    }

}
