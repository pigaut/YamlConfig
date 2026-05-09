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
        return new Composer(settings, new ParserImpl(settings, streamReader));
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
        Iterator<Node> result = new YamlIterator(composer);
        return new YamlIterable(result);
    }

    public Iterable<Node> loadAllFromInputStream(InputStream yamlStream) {
        Objects.requireNonNull(yamlStream, "InputStream cannot be null");
        Composer composer =
                createComposer(new StreamReader(settings, new YamlUnicodeReader(yamlStream)));
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

    private static class YamlIterable implements Iterable<Node> {

        private final Iterator<Node> iterator;

        public YamlIterable(Iterator<Node> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<Node> iterator() {
            return iterator;
        }
    }

    private static class YamlIterator implements Iterator<Node> {

        private final Composer composer;
        private boolean composerInitiated = false;

        public YamlIterator(Composer composer) {
            this.composer = composer;
        }

        @Override
        public boolean hasNext() {
            composerInitiated = true;
            return composer.hasNext();
        }

        @Override
        public Node next() {
            if (!composerInitiated) {
                hasNext();
            }
            return composer.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removing is not supported.");
        }
    }

}
