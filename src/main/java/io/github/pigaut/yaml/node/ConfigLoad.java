package io.github.pigaut.yaml.node;

import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.composer.*;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.parser.*;
import org.snakeyaml.engine.v2.scanner.*;

import java.io.*;
import java.util.*;

public class ConfigLoad extends Load {

    public ConfigLoad() {
        super(LoadSettings.builder().setParseComments(true).build());
    }

    protected @Nullable Node loadOneNode(Composer composer) {
        Optional<Node> nodeOptional = composer.getSingleNode();
        return nodeOptional.orElse(null);
    }

    public @Nullable Node loadNodeFromInputStream(@NotNull InputStream yamlStream) {
        Objects.requireNonNull(yamlStream, "InputStream cannot be null");
        return loadOneNode(createComposer(yamlStream));
    }

    public @Nullable Node loadNodeFromReader(@NotNull Reader yamlReader) {
        Objects.requireNonNull(yamlReader, "Reader cannot be null");
        return loadOneNode(createComposer(yamlReader));
    }

    public @Nullable Node loadNodeFromString(@NotNull String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        return loadOneNode(createComposer(yaml));
    }

    private Iterable<Node> loadAllNodes(Composer composer) {
        return () -> composer;
    }

    public Iterable<Node> loadAllNodesFromInputStream(InputStream yamlStream) {
        Objects.requireNonNull(yamlStream, "InputStream cannot be null");
        Composer composer = createComposer(yamlStream);
        return loadAllNodes(composer);
    }

    public Iterable<Node> loadAllNodesFromReader(Reader yamlReader) {
        Objects.requireNonNull(yamlReader, "Reader cannot be null");
        Composer composer = createComposer(yamlReader);
        return loadAllNodes(composer);
    }

    public Iterable<Node> loadAllNodesFromString(String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        Composer composer = createComposer(yaml);
        return loadAllNodes(composer);
    }

}
