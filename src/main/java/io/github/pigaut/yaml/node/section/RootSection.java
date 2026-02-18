package io.github.pigaut.yaml.node.section;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.*;
import org.snakeyaml.engine.v2.exceptions.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;

public class RootSection extends Section implements ConfigRoot {

    private final @Nullable File file;
    private final String name;
    private final Load loader = new ConfigLoad();
    private final Dump dumper = new ConfigDump();
    private @NotNull Configurator configurator;
    private @Nullable String prefix;
    private @NotNull String header = "";
    private final Deque<String> problems = new LinkedList<>();

    public RootSection(@NotNull Configurator configurator) {
        this(null, configurator, null);
    }

    public RootSection(@Nullable File file, @NotNull Configurator configurator) {
        this(file, configurator, null);
    }

    public RootSection(@Nullable File file, @NotNull Configurator configurator, @Nullable String prefix) {
        super(FlowStyle.BLOCK);
        Preconditions.checkNotNull(configurator, "Configurator cannot be null");
        this.file = file;
        this.name = file != null ? YamlConfig.getFileName(file) : null;
        this.configurator = configurator;
        this.prefix = prefix;
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
    public @Nullable String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
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
    public void load() throws ConfigLoadException {
        Preconditions.checkState(file != null, "Cannot load configuration from file because file is null");
        load(file);
    }

    @Override
    public void load(@NotNull Consumer<ConfigLoadException> errorCollector) {
        try {
            load();
        } catch (ConfigLoadException e) {
            errorCollector.accept(e);
        }
    }

    @Override
    public void load(@NotNull File file) throws ConfigLoadException {
        if (!file.exists()) {
            throw new ConfigLoadException(this, "File does not exist");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            load(reader);
        } catch (IOException e) {
            throw new ConfigLoadException(this, e.getMessage());
        }
    }

    @Override
    public void load(@NotNull InputStream inputStream) throws ConfigLoadException {
        Object parsedNode;
        try {
            parsedNode = loader.loadFromInputStream(inputStream);
        } catch (ParserException | ScannerException | ComposerException e) {
            throw new ConfigLoadException(this, e.getMessage());
        }
        load(parsedNode);
    }

    @Override
    public void load(@NotNull Reader reader) throws ConfigLoadException {
        Object parsedNode;
        try {
            parsedNode = loader.loadFromReader(reader);
        } catch (ParserException | ScannerException | ComposerException e) {
            throw new ConfigLoadException(this, e.getMessage());
        }
        load(parsedNode);
    }

    private void load(Object parsedNode) throws ConfigLoadException {
        if (!(parsedNode instanceof Map<?, ?> map)) {
            throw new ConfigLoadException(this, "Expected a section but found another node");
        }
        clear();
        map.forEach((key, value) -> {
            set(String.valueOf(key), value);
        });
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
        if (!YamlConfig.createFileIfNotExists(file)) {
            return false;
        }

        String yamlData = saveToString();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(yamlData);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public String saveToString() {
        return header + dumper.dumpToString(this);
    }

    @Override
    public @NotNull RootSequence convertToSequence() {
        return new RootSequence(file, configurator, prefix);
    }

}
