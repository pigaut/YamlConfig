package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.exceptions.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public class RootScalar extends Scalar implements ConfigRoot {

    private final @Nullable File file;
    private final @Nullable String name;
    private final Load loader = new ConfigLoad();
    private final Deque<String> problems = new LinkedList<>();
    private @NotNull Configurator configurator;
    private @Nullable String prefix;
    private boolean debug;
    private @NotNull String header = "";

    public RootScalar(@Nullable File file, @NotNull Configurator configurator, @Nullable String prefix, boolean debug) {
        super("");
        Preconditions.checkNotNull(configurator, "Configurator cannot be null");
        this.file = file;
        this.name = file != null ? YamlConfig.getFileName(file) : null;
        this.configurator = configurator;
        this.prefix = prefix;
        this.debug = debug;
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
    public boolean isDebug() {
        return debug;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
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
    public boolean load() {
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
        final Object data = loader.loadFromInputStream(inputStream);
        this.clear();

        if (YamlConfig.isScalarType(data.getClass())) {
            setValue(data);
            return true;
        }
        return false;
    }

    @Override
    public boolean load(@NotNull Reader reader) {
        final Object data = loader.loadFromReader(reader);
        this.clear();

        if (YamlConfig.isScalarType(data.getClass())) {
            setValue(data);
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
            throw new InvalidConfigurationException(this, e.getMessage());
        }
    }

    @Override
    public String saveToString() {
        return header + this.toString();
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        final ConfigSequence sequence = new RootSequence(file, configurator, prefix, debug);
        final List<Object> parsedValues = ParseUtil.parseAllAsScalars(pattern.split(toString()));
        sequence.map(parsedValues);
        return sequence;
    }

}
