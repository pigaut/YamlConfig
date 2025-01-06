package io.github.pigaut.yaml.node.sequence;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.section.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.*;
import org.snakeyaml.engine.v2.exceptions.*;

import java.io.*;
import java.util.*;

public class RootSequence extends Sequence implements ConfigRoot {

    private final File file;
    private final String name;
    private @NotNull Configurator configurator;
    private @Nullable String prefix = null;
    private boolean debug = true;
    private @NotNull String header = "";

    private final Load loader = new ConfigLoad();
    private final Dump dumper = new ConfigDump();

    public RootSequence(@NotNull File file) {
        this(file, new StandardConfigurator());
    }

    public RootSequence(@NotNull File file, @NotNull Configurator configurator) {
        super(FlowStyle.BLOCK);
        this.file = file;
        this.name = YamlConfig.getFileName(file);
        this.configurator = configurator;
    }

    public RootSequence(@NotNull File file, @NotNull Configurator configurator, @NotNull List<@NotNull Object> elements) {
        super(FlowStyle.BLOCK);
        this.file = file;
        this.name = YamlConfig.getFileName(file);
        this.configurator = configurator;
        this.map(elements);
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public @NotNull RootSequence getRoot() {
        return this;
    }

    @Override
    public @NotNull String getKey() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Root does not have a key");
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
    public @NotNull RootSection convertToSection() {
        return new RootSection(file, configurator, toMap());
    }

    @Override
    public @NotNull File getFile() {
        return file;
    }

    @Override
    public @NotNull String getName() {
        return name;
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
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public @Nullable String getHeader() {
        return header;
    }

    @Override
    public void setHeader(@NotNull String... lines) {
        this.header = YamlConfig.createHeader(lines);
    }

    @Override
    public boolean load() throws ConfigurationLoadException {
        try {
            return load(file);
        } catch (ParserException e) {
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
        final List<Object> documents = new ArrayList<>();
        loader.loadAllFromInputStream(inputStream).forEach(documents::add);
        return loadDocuments(documents);
    }

    @Override
    public boolean load(@NotNull Reader reader) {
        final List<Object> documents = new ArrayList<>();
        loader.loadAllFromReader(reader).forEach(documents::add);
        return loadDocuments(documents);
    }

    private boolean loadDocuments(List<Object> documents) {
        this.clear();
        if (documents.isEmpty()) {
            return false;
        }
        if (documents.size() == 1) {
            final Object data = documents.get(0);
            if (data instanceof List<?> elements) {
                elements.forEach(this::add);
                setFlowStyle(FlowStyle.BLOCK);
                return true;
            }
            return false;
        }

        for (Object data : documents) {
            if (data != null) {
                this.add(data);
            }
        }
        setFlowStyle(FlowStyle.AUTO);
        return true;
    }

    @Override
    public boolean save() {
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
           throw new InvalidConfigurationException(this, e.getMessage());
        }
    }

    @Override
    public String saveToString() {
        if (this.getFlowStyle() == FlowStyle.AUTO) {
            return header + dumper.dumpAllToString(this.iterator());
        }
        return header + dumper.dumpToString(this);
    }

}
