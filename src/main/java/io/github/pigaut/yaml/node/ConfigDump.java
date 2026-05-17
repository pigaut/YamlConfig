package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.emitter.*;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.serializer.*;

import java.io.*;
import java.util.*;

public class ConfigDump {

    private final DumpSettings settings = DumpSettings.builder()
            .setDumpComments(true)
            .setIndentWithIndicator(true)
            .setIndicatorIndent(2)
            .setWidth(100)
            .build();

    public @NotNull String dumpToString(@NotNull ConfigRoot root) {
        Node rootNode = root.get(Node.class).orElse(null);
        if (rootNode == null) {
            return "";
        }

        StreamDataWriter writer = new StringStreamDataWriter();
        Emitter emitter = new Emitter(settings, writer);
        Serializer serializer = new Serializer(settings, emitter);

        try {
            serializer.emitStreamStart();
            serializer.serializeDocument(rootNode);
            serializer.emitStreamEnd();
        } catch (Exception e) {
            return "";
        }

        return writer.toString();
    }

    public @NotNull String dumpAllToString(@NotNull Iterator<KeylessField> fields) {
        StreamDataWriter writer = new StringStreamDataWriter();
        Emitter emitter = new Emitter(settings, writer);
        Serializer serializer = new Serializer(settings, emitter);

        try {
            serializer.emitStreamStart();

            while (fields.hasNext()) {
                ConfigField field = fields.next();
                Node node = field.get(Node.class).orElse(null);
                if (node != null) {
                    serializer.serializeDocument(node);
                }
            }

            serializer.emitStreamEnd();
        } catch (Exception e) {
            return "";
        }

        return writer.toString();
    }

    private static class StringStreamDataWriter implements StreamDataWriter {
        private final StringWriter writer = new StringWriter();

        @Override
        public void write(String str) {
            writer.write(str);
        }

        @Override
        public void write(String str, int off, int len) {
            writer.write(str, off, len);
        }

        @Override
        public String toString() {
            return writer.toString();
        }
    }

}
