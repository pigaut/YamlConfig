package io.github.pigaut.yaml.node;

import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.composer.*;
import org.snakeyaml.engine.v2.constructor.*;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.parser.*;
import org.snakeyaml.engine.v2.scanner.*;

import java.io.*;
import java.util.*;

public class ConfigLoad {

    private final LoadSettings settings = LoadSettings.builder()
            .setParseComments(true)
            .build();

    public ConfigLoad() {

    }

    private Composer createComposer(StreamReader streamReader) {
        Parser fixingParser = new CommentFixingParser(settings, streamReader);
        return new Composer(settings, fixingParser);
    }

    protected Composer createComposer(InputStream yamlStream) {
        return createComposer(new StreamReader(settings, new YamlUnicodeReader(yamlStream)));
    }

    protected Composer createComposer(String yaml) {
        return createComposer(new StreamReader(settings, yaml));
    }

    protected Composer createComposer(Reader yamlReader) {
        return createComposer(new StreamReader(settings, yamlReader));
    }

    protected @Nullable Node loadOne(Composer composer) {
        Optional<Node> nodeOptional = composer.getSingleNode();
        return nodeOptional.orElse(null);
    }

    public @Nullable Node loadFromInputStream(@NotNull InputStream yamlStream) {
        Objects.requireNonNull(yamlStream, "InputStream cannot be null");
        return loadOne(createComposer(yamlStream));
    }

    public @Nullable Node loadFromReader(@NotNull Reader yamlReader) {
        Objects.requireNonNull(yamlReader, "Reader cannot be null");
        return loadOne(createComposer(yamlReader));
    }

    public @Nullable Node loadFromString(@NotNull String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        return loadOne(createComposer(yaml));
    }

    private Iterable<Node> loadAll(Composer composer) {
        return () -> composer;
    }

    public Iterable<Node> loadAllFromInputStream(InputStream yamlStream) {
        Objects.requireNonNull(yamlStream, "InputStream cannot be null");
        Composer composer = createComposer(new StreamReader(settings, new YamlUnicodeReader(yamlStream)));
        return loadAll(composer);
    }

    public Iterable<Node> loadAllFromReader(Reader yamlReader) {
        Objects.requireNonNull(yamlReader, "Reader cannot be null");
        Composer composer = createComposer(new StreamReader(settings, yamlReader));
        return loadAll(composer);
    }

    public Iterable<Node> loadAllFromString(String yaml) {
        Objects.requireNonNull(yaml, "String cannot be null");
        Composer composer = createComposer(new StreamReader(settings, yaml));
        return loadAll(composer);
    }

}
