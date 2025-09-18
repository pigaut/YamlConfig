package io.github.pigaut.yaml.node.line;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.optional.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.regex.*;

public abstract class LineScalar implements ConfigScalar {

    protected final ConfigLine line;
    protected @NotNull Object value;

    public LineScalar(@NotNull ConfigLine line, @NotNull Object value) {
        this.line = line;
        this.value = value;
    }

    @Override
    public @NotNull Object getValue() {
        return value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value != null) {
            if (!YamlConfig.isScalarType(value.getClass())) {
                throw new IllegalArgumentException("Value is not a scalar");
            }
            this.value = value;
        } else {
            this.value = "";
        }
    }

    @Override
    public @NotNull ScalarStyle getScalarStyle() {
        return line.asScalar().getScalarStyle();
    }

    @Override
    public void setScalarStyle(@NotNull ScalarStyle scalarStyle) {
        line.asScalar().setScalarStyle(scalarStyle);
    }

    @Override
    public @NotNull String toString(@NotNull StringFormatter formatter) {
        return formatter.format(toString());
    }

    @Override
    public ConfigLine toLine() {
        return line;
    }

    @Override
    public ConfigSequence split(Pattern pattern) {
        return line.asScalar().split(pattern);
    }

    @Override
    public void clear() {
        line.clear();
    }

    @Override
    public boolean isRoot() {
        return line.isRoot();
    }

    @Override
    public @NotNull ConfigBranch getParent() throws UnsupportedOperationException {
        return line.getParent();
    }

    @Override
    public @NotNull ConfigRoot getRoot() {
        return line.getRoot();
    }

    @Override
    public @Nullable String getPath() {
        return line.getPath();
    }

    @Override
    public @Nullable String getSimplePath() {
        return line.getSimplePath();
    }

    @Override
    public @NotNull FieldType getFieldType() {
        return line.getFieldType();
    }

    @Override
    public <T> T loadRequired(Class<T> classType) {
        return load(classType).orThrow();
    }

    @Override
    public <T> ConfigOptional<T> load(@NotNull Class<T> classType) {
        final ConfigRoot root = this.getRoot();
        final Configurator configurator = root.getConfigurator();

        final ConfigLoader<? extends T> loader = configurator.getLoader(classType);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class type: " + classType.getSimpleName());
        }

        final String problemDescription = loader.getProblemDescription();
        root.addProblem(problemDescription);

        try {
            loader.loadFromScalar(this);
            return ConfigOptional.of(this, loader.loadFromScalar(this));
        }
        catch (InvalidConfigurationException e) {
            return ConfigOptional.invalid(e);
        }
        finally {
            root.removeProblem(problemDescription);
        }
    }

    @Override
    public <T> void map(T value) {
        line.map(value);
    }

    @Override
    public ConfigOptional<ConfigScalar> toScalar() {
        return line.toScalar();
    }

    @Override
    public ConfigOptional<ConfigSection> toSection() {
        return line.toSection();
    }

    @Override
    public ConfigOptional<ConfigSequence> toSequence() {
        return line.toSequence();
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }

}
