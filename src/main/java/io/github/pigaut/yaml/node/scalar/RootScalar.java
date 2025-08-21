package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.sequence.*;
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
    private @NotNull Configurator configurator;
    private @Nullable String prefix = null;
    private boolean debug = true;
    private @NotNull String header = "";
    private final Deque<String> problems = new LinkedList<>();

    public RootScalar() {
        this(null, new StandardConfigurator());
    }

    public RootScalar(@Nullable File file) {
        this(file, new StandardConfigurator());
    }

    public RootScalar(@NotNull Configurator configurator) {
        this(null, configurator);
    }

    public RootScalar(@Nullable File file, @NotNull Configurator configurator) {
        super("");
        this.file = file;
        this.name = file != null ? YamlConfig.getFileName(file) : null;
        this.configurator = configurator;
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
    public @NotNull RootScalar getRoot() {
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
    public ConfigSequence split(String regex) {
        final ConfigSequence sequence = new RootSequence(file, configurator);
        final List<Object> parsedValues = ParseUtil.parseAllAsScalars(this.toString().split(regex));
        sequence.map(parsedValues);
        return sequence;
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        Matcher matcher = pattern.matcher(this.toString());
        List<String> parts = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    parts.add(matcher.group(i));
                    break;
                }
            }
        }
        final ConfigSequence sequence = new RootSequence(file, configurator);
        final List<Object> parsedValues = ParseUtil.parseAllAsScalars(parts);
        sequence.map(parsedValues);
        return sequence;
    }

}
