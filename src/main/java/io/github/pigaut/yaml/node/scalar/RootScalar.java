package io.github.pigaut.yaml.node.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.exceptions.*;

import java.io.*;

public class RootScalar extends Scalar implements ConfigRoot {

    private final File file;
    private final String name;
    private final Load loader = new ConfigLoad();
    private @NotNull Configurator configurator;
    private @Nullable String prefix = null;
    private boolean debug = true;
    private @NotNull String header = "";

    public RootScalar(@NotNull File file, @NotNull Object value) {
        this(file, new StandardConfigurator(), value);
    }

    public RootScalar(@NotNull File file, @NotNull Configurator configurator, @NotNull Object value) {
        super(value);
        this.file = file;
        this.name = YamlConfig.getFileName(file);
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
    public @NotNull File getFile() {
        return file;
    }

    @Override
    public @NotNull String getName() {
        return name;
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
    public boolean load() {
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
            throw new InvalidConfigurationException(this, e.getMessage());
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

}
