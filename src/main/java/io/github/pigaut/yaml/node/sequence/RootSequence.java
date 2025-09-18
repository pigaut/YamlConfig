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
import java.nio.charset.*;
import java.util.*;

public class RootSequence extends Sequence implements ConfigRoot {

    private final @Nullable File file;
    private final @Nullable String name;
    private final Load loader = new ConfigLoad();
    private final Dump dumper = new ConfigDump();
    private @NotNull Configurator configurator;
    private @Nullable String prefix;
    private boolean debug;
    private @NotNull String header = "";
    private final Deque<String> problems = new LinkedList<>();

    public RootSequence(@Nullable File file, @NotNull Configurator configurator, @Nullable String prefix, boolean debug) {
        super(FlowStyle.BLOCK);
        this.file = file;
        this.name = file != null ? YamlConfig.getFileName(file) : null;
        this.configurator = configurator;
        this.prefix = prefix;
        this.debug = debug;
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
    public @NotNull RootSequence getRoot() {
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
        } catch (ParserException | ScannerException | ComposerException e) {
            throw new ConfigurationLoadException(this, e);
        }
    }

    @Override
    public boolean load(@NotNull File file) {
        if (!file.exists()) {
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
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

    @Override
    public @NotNull RootSection convertToSection() {
        return new RootSection(file, configurator, prefix, debug);
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

}
