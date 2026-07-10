package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.exceptions.*;
import org.snakeyaml.engine.v2.nodes.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;

public class RootScalar extends Scalar implements ConfigRoot {

    private final ConfigLoad loader = new ConfigLoad();
    private final ConfigDump dumper = new ConfigDump();
    private Configurator configurator;
    private String header = "";

    private final @Nullable File file;
    private final @Nullable String name;
    private @Nullable String prefix;

    public RootScalar(@NotNull Configurator configurator) {
        this(null, configurator, null);
    }

    public RootScalar(@Nullable File file, @NotNull Configurator configurator) {
        this(file, configurator, null);
    }

    public RootScalar(@Nullable File file, @NotNull Configurator configurator, @Nullable String prefix) {
        super("");
        Preconditions.checkNotNull(configurator, "Configurator cannot be null");
        this.file = file;
        this.name = file != null ? YamlConfig.getFileName(file) : null;
        this.configurator = configurator;
        this.prefix = prefix;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public @NotNull RootScalar getRoot() {
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
    public boolean hasFile() {
        return file != null;
    }

    @Override
    public @Nullable File getFile() {
        return file;
    }

    @Override
    public @Nullable String getName() {
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
        loadFromFile(file);
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
    public void loadFromFile(@NotNull File file) throws ConfigLoadException {
        if (!file.exists()) {
            throw new ConfigLoadException(this, "File does not exist");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            loadFromReader(reader);
        } catch (IOException e) {
            throw new ConfigLoadException(this, e.getMessage());
        }
    }

    @Override
    public void loadFromStream(@NotNull InputStream inputStream) throws ConfigLoadException {
        Node node;
        try {
            node = loader.loadFromInputStream(inputStream);
        } catch (YamlEngineException e) {
            throw new ConfigLoadException(this, e.getMessage());
        }
        load(node);
    }

    @Override
    public void loadFromReader(@NotNull Reader reader) throws ConfigLoadException {
        Node node;
        try {
            node = loader.loadFromReader(reader);
        } catch (YamlEngineException e) {
            throw new ConfigLoadException(this, e.getMessage());
        }
        load(node);
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
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String saveToString() {
        return header + dumper.dumpToString(this);
    }

    private void load(Node node) throws ConfigLoadException {
        if (!(node instanceof ScalarNode)) {
            throw new ConfigLoadException(this, "Expected a scalar but found another node");
        }
        setValue(node);
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        ConfigSequence sequence = new RootSequence(file, configurator, prefix);
        List<Object> parsedValues = ParseUtil.parseAllAsScalars(Tag.STR, pattern.split(toString()));
        sequence.map(parsedValues);
        return sequence;
    }

}
