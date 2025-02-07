package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.*;
import org.snakeyaml.engine.v2.exceptions.*;

import java.io.*;
import java.util.*;

public class RootSection extends Section implements ConfigRoot {

    private final @Nullable File file;
    private final String name;
    private final Load loader = new ConfigLoad();
    private final Dump dumper = new ConfigDump();
    private @NotNull Configurator configurator;
    private @Nullable String prefix = null;
    private boolean debug = true;
    private @NotNull String header = "";
    private final Deque<String> problems = new LinkedList<>();

    public RootSection() {
        this(null, new StandardConfigurator());
    }

    public RootSection(@Nullable File file) {
        this(file, new StandardConfigurator());
    }

    public RootSection(@NotNull Configurator configurator) {
        this(null, configurator);
    }

    public RootSection(@Nullable File file, @NotNull Configurator configurator) {
        super(FlowStyle.BLOCK);
        this.file = file;
        this.name = file != null ? YamlConfig.getFileName(file) : null;
        this.configurator = configurator;
    }

    public RootSection(@Nullable File file, @NotNull Configurator configurator, @NotNull Map<String, @NotNull Object> mappings) {
        this(file, configurator);
        this.map(mappings);
    }

    @Override
    public @NotNull String getKey() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Root does not have a key");
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public @NotNull RootSection getRoot() {
        return this;
    }

    @Override
    public @Nullable String getCurrentProblem() {
        return problems.peekLast();
    }

    @Override
    public void addProblem(String problemDescription) {
        if (problemDescription != null) {
            problems.add(problemDescription);
        }
    }

    @Override
    public void removeProblem(String problemDescription) {
        if (problemDescription != null) {
            problems.remove(problemDescription);
        }
    }

    @Override
    public @NotNull Branch getParent() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Root does not have a parent");
    }

    @Override
    public @Nullable String getPath() {
        return null;
    }

    @Override
    public @NotNull String getPath(String key) {
        return key;
    }

    @Override
    public @Nullable String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
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
    public @Nullable File getFile() {
        return file;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getHeader() {
        return header;
    }

    @Override
    public void setHeader(@NotNull String... lines) {
        this.header = YamlConfig.createHeader(lines);
    }

    @Override
    public boolean load() throws ConfigurationLoadException {
        if (file == null) {
            throw new IllegalStateException("You cannot load configuration from file because file is null");
        }
        try {
            return load(file);
        } catch (ParserException | ScannerException e) {
            throw new ConfigurationLoadException(this, e);
        }
    }

    @Override
    public boolean load(@NotNull File file) {
        if (!file.exists()) {
            return false;
        }

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            return load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean load(@NotNull InputStream inputStream) {
        final Object data = loader.loadFromInputStream(inputStream);
        ;
        this.clear();

        if (data instanceof Map<?, ?> map) {
            map.forEach((key, value) -> {
                set(String.valueOf(key), value);
            });
            return true;
        }

        return false;
    }

    @Override
    public boolean load(@NotNull Reader reader) {
        final Object data = loader.loadFromReader(reader);
        this.clear();

        if (data instanceof Map<?, ?> map) {
            map.forEach((key, value) -> {
                set(String.valueOf(key), value);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean save() {
        if (file == null) {
            throw new IllegalStateException("You cannot save configuration to file because file is null");
        }
        return save(file);
    }

    @Override
    public boolean save(@NotNull File file) {
        if (!YamlConfig.createFileIfNotExists(file))
            return false;
        final String yamlData = saveToString();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(yamlData);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String saveToString() {
        return header + dumper.dumpToString(this);
    }

    @Override
    public @NotNull RootSequence convertToSequence() {
        return new RootSequence(file, configurator, toList());
    }

}
