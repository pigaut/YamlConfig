package io.github.pigaut.yaml.node.line.scalar;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.load.*;
import io.github.pigaut.yaml.convert.format.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.line.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;
import org.snakeyaml.engine.v2.common.*;

import java.util.*;
import java.util.regex.*;

public abstract class LineScalar implements ConfigScalar {

    protected final ConfigLine line;
    protected @NotNull Object value;

    public LineScalar(@NotNull ConfigLine line, @NotNull Object value) {
        this.line = line;
        this.value = value;
    }

    @Override
    public boolean isInLine() {
        return true;
    }

    @Override
    public boolean contains(String value) {
        String string = toString();
        return string.contains(value);
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
    public boolean equals(@NotNull String value) {
        String string = toString();
        return string.equals(value);
    }

    @Override
    public boolean equalsIgnoreCase(@NotNull String value) {
        String string = toString();
        return string.equalsIgnoreCase(value);
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
    public ConfigLine toLine(@NotNull LineStyle lineStyle) {
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
    public @NotNull String getPath() {
        return line.getPath();
    }

    @Override
    public @NotNull String getSimplePath() {
        return line.getSimplePath();
    }

    @Override
    public @NotNull FieldType getFieldType() {
        return line.getFieldType();
    }

    @Override
    public <T> T getRequired(@NotNull Class<T> classType) throws InvalidConfigException {
        return get(classType).orThrow();
    }

    @Override
    public <T> ConfigOptional<T> get(@NotNull Class<T> classType) {
        ConfigRoot root = this.getRoot();
        Configurator configurator = root.getConfigurator();

        ConfigLoader<? extends T> loader = configurator.getLoader(classType);
        if (loader == null) {
            throw new IllegalArgumentException("No config loader found for class type: " + classType.getSimpleName());
        }

        try {
            loader.loadFromScalar(this);
            return ConfigOptional.of(this, loader.loadFromScalar(this));
        } catch (InvalidConfigException e) {
            e.setError(loader.getErrorDescription());
            return ConfigOptional.invalid(e);
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

    @Override
    public List<CommentLine> getBlockComments() {
        return line.getBlockComments();
    }

    @Override
    public void setBlockComments(@NotNull List<CommentLine> blockComments) {
        line.setBlockComments(blockComments);
    }

    @Override
    public void clearBlockComments() {
        line.clearBlockComments();
    }

    @Override
    public void addBlockComment(@NotNull String value) {
        line.addBlockComment(value);
    }

    @Override
    public void addBlockBlankLine() {
        line.addBlockBlankLine();
    }

    @Override
    public List<CommentLine> getInLineComments() {
        return line.getInLineComments();
    }

    @Override
    public void setInLineComments(@NotNull List<CommentLine> inLineComments) {
        line.setInLineComments(inLineComments);
    }

    @Override
    public void clearInlineComments() {
        line.clearInlineComments();
    }

    @Override
    public void addInlineComment(@NotNull String value) {
        line.addInlineComment(value);
    }
}
