package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.FieldType;
import io.github.pigaut.yaml.node.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.comments.*;

import java.util.*;

public interface ConfigField {

    void clear();
    boolean isRoot();
    @NotNull ConfigBranch getParent() throws UnsupportedOperationException;
    @NotNull ConfigRoot getRoot();
    @NotNull String getKey();
    @NotNull Object getValue();
    @NotNull String getPath();
    @NotNull String getSimplePath();
    @NotNull FieldType getFieldType();

    <T> T getRequired(@NotNull Class<T> classType) throws InvalidConfigException;
    <T> ConfigOptional<T> get(@NotNull Class<T> classType);

    <T> void map(T value);

    void replaceAll(@NotNull CharSequence target, @NotNull CharSequence replacement);

    List<CommentLine> getBlockComments();
    void setBlockComments(@Nullable List<CommentLine> blockComments);
    void clearBlockComments();
    void addBlockComment(@NotNull String value);
    void addBlockBlankLine();

    List<CommentLine> getInLineComments();
    void setInLineComments(@Nullable List<CommentLine> inLineComments);
    void clearInlineComments();
    void addInlineComment(@NotNull String value);

    ConfigOptional<ConfigScalar> toScalar();
    ConfigOptional<ConfigSection> toSection();
    ConfigOptional<ConfigSequence> toSequence();

}
