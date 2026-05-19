package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.exceptions.*;
import org.snakeyaml.engine.v2.nodes.*;

import java.io.*;
import java.math.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

public class YamlConfig {

    private static final ConfigLoad loader = new ConfigLoad();

    private YamlConfig() {}

    @NotNull
    public static ConfigRoot loadConfig(@NotNull File file) throws ConfigLoadException {
        return loadConfig(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static ConfigRoot loadConfig(@NotNull File file, @NotNull Configurator configurator) throws ConfigLoadException {
        return loadConfig(file, configurator, null);
    }

    @NotNull
    public static ConfigRoot loadConfig(@NotNull File file, @NotNull Configurator configurator, String prefix) throws ConfigLoadException {
        List<Node> documents = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            for (Node document : loader.loadAllFromReader(reader)) {
                documents.add(document);
            }
        } catch (IOException | YamlEngineException e) {
            throw new ConfigLoadException(prefix, file, e.getMessage());
        }

        if (documents.isEmpty()) {
            throw new ConfigLoadException(prefix, file, "Could not determine config type");
        }

        if (documents.size() > 1) {
            RootSequence sequence = new RootSequence(file, configurator, prefix);
            for (Node node : documents) {
                if (node == null || (node instanceof ScalarNode scalar && scalar.getValue().isEmpty())) {
                    continue;
                }
                sequence.add(node);
            }
            return sequence;
        }

        Node node = documents.get(0);
        if (node instanceof MappingNode) {
            RootSection section = new RootSection(file, configurator, prefix);
            section.map(node);
            return section;
        }

        if (node instanceof SequenceNode) {
            RootSequence sequence = new RootSequence(file, configurator, prefix);
            sequence.map(node);
            return sequence;
        }

        if (node instanceof ScalarNode) {
            RootScalar scalar = new RootScalar(file, configurator, prefix);
            scalar.map(node);
            return scalar;
        }

        throw new ConfigLoadException(prefix, file, "Could not determine config type");
    }

    @NotNull
    public static RootSection loadSection(@NotNull File file) throws ConfigLoadException {
        return loadSection(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSection loadSectionOrEmpty(@NotNull File file) {
        return loadSectionOrEmpty(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSection loadSection(@NotNull File file, @NotNull Configurator configurator) throws ConfigLoadException {
        return loadSection(file, configurator, null);
    }

   @NotNull
    public static RootSection loadSectionOrEmpty(@NotNull File file, @NotNull Configurator configurator) {
        return loadSectionOrEmpty(file, configurator, null);
    }

    @NotNull
    public static RootSection loadSection(@NotNull File file, @NotNull Configurator configurator, String prefix) throws ConfigLoadException {
        RootSection section = createEmptySection(file, configurator, prefix);
        section.load();
        return section;
    }

    @NotNull
    public static RootSection loadSectionOrEmpty(@NotNull File file, @NotNull Configurator configurator, String prefix) {
        RootSection section = createEmptySection(file, configurator, prefix);
        try {
            section.load();
        } catch (ConfigLoadException e) {
            // ignored
        }
        return section;
    }

    @NotNull
    public static RootSection createEmptySection() {
        return new RootSection(null, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSection createEmptySection(@NotNull File file) {
        return new RootSection(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSection createEmptySection(@NotNull File file, @NotNull Configurator configurator) {
        return new RootSection(file, configurator, null);
    }

    @NotNull
    public static RootSection createEmptySection(@NotNull File file, @NotNull Configurator configurator, String prefix) {
        return new RootSection(file, configurator, prefix);
    }

    @NotNull
    public static RootSequence loadSequence(@NotNull File file) throws ConfigLoadException {
        return loadSequence(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSequence loadSequenceOrEmpty(@NotNull File file) {
        return loadSequenceOrEmpty(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSequence loadSequence(@NotNull File file, @NotNull Configurator configurator) throws ConfigLoadException {
        return loadSequence(file, configurator, null);
    }

    @NotNull
    public static RootSequence loadSequenceOrEmpty(@NotNull File file, @NotNull Configurator configurator) {
        return loadSequenceOrEmpty(file, configurator, null);
    }

    @NotNull
    public static RootSequence loadSequence(@NotNull File file, @NotNull Configurator configurator, String prefix) throws ConfigLoadException {
        RootSequence sequence = createEmptySequence(file, configurator, prefix);
        sequence.load();
        return sequence;
    }

    @NotNull
    public static RootSequence loadSequenceOrEmpty(@NotNull File file, @NotNull Configurator configurator, String prefix) {
        RootSequence sequence = createEmptySequence(file, configurator, prefix);
        try {
            sequence.load();
        } catch (ConfigLoadException e) {
            // ignored
        }
        return sequence;
    }

    @NotNull
    public static RootSequence createEmptySequence() {
        return new RootSequence(null, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSequence createEmptySequence(@NotNull File file) {
        return new RootSequence(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSequence createEmptySequence(@NotNull File file, @NotNull Configurator configurator) {
        return new RootSequence(file, configurator, null);
    }

    @NotNull
    public static RootSequence createEmptySequence(@NotNull File file, @NotNull Configurator configurator, String prefix) {
        return new RootSequence(file, configurator, prefix);
    }

    @NotNull
    public static RootScalar loadScalar(@NotNull File file) throws ConfigLoadException {
        return loadScalar(file, new StandardConfigurator(), (String) null);
    }

    @NotNull
    public static RootScalar loadScalarOrEmpty(@NotNull File file) {
        return loadScalarOrEmpty(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootScalar loadScalar(@NotNull File file, @NotNull Configurator configurator) throws ConfigLoadException {
        return loadScalar(file, configurator, (String) null);
    }

    @NotNull
    public static RootScalar loadScalarOrEmpty(@NotNull File file, @NotNull Configurator configurator) {
        return loadScalarOrEmpty(file, configurator, null);
    }

    @NotNull
    public static RootScalar loadScalar(@NotNull File file, @NotNull Configurator configurator, String prefix) throws ConfigLoadException {
        RootScalar scalar = createEmptyScalar(file, configurator, prefix);
        scalar.load();
        return scalar;
    }

    @NotNull
    public static RootScalar loadScalarOrEmpty(@NotNull File file, @NotNull Configurator configurator, String prefix) {
        RootScalar scalar = createEmptyScalar(file, configurator, prefix);
        try {
            scalar.load();
        } catch (ConfigLoadException e) {
            // ignored
        }
        return scalar;
    }

    @NotNull
    public static RootScalar createEmptyScalar() {
        return new RootScalar(null, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootScalar createEmptyScalar(@NotNull File file) {
        return new RootScalar(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootScalar createEmptyScalar(@NotNull File file, @NotNull Configurator configurator) {
        return new RootScalar(file, configurator, null);
    }

    @NotNull
    public static RootScalar createEmptyScalar(@NotNull File file, @NotNull Configurator configurator, String prefix) {
        return new RootScalar(file, configurator, prefix);
    }

    public static final List<Class<?>> SCALARS = List.of(
            Boolean.class, Character.class, String.class,
            Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class, BigInteger.class, BigDecimal.class
    );

    public static String getFileName(@NotNull File file) {
        final String fileName = file.getName();
        final int extension = fileName.lastIndexOf(".");
        return extension != -1 ? fileName.substring(0, extension) : fileName;
    }

    public static String generateRandomKey() {
        return UUID.randomUUID().toString();
    }
    
    public static boolean isScalarType(Class<?> classType) {
        return SCALARS.contains(classType);
    }

    public static boolean isScalar(Object value) {
        return value == null || isScalarType(value.getClass());
    }

    public static String createHeader(@NotNull String... lines) {
        if (lines.length == 0) {
            return "";
        }
        return Arrays.stream(lines)
                .map(line -> line.isEmpty() ? "\n" : "#" + line)
                .collect(Collectors.joining("\n"));
    }

    public static boolean createFileIfNotExists(@NotNull File file) {
        if (file.exists()) {
            return file.isFile();
        }

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            return file.createNewFile();
        }
        catch (IOException e) {
            return false;
        }
    }

    public static boolean isYamlFile(@NotNull File file) {
        return isYamlFile(file.getName());
    }

    public static boolean isYamlFile(@NotNull String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        return lower.endsWith(".yml") || lower.endsWith(".yaml");
    }

    public static @NotNull String ensureYamlExtension(@NotNull String fileName) {
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".yml") || lower.endsWith(".yaml")) {
            return fileName;
        }

        return fileName + ".yml";
    }

    // Handle lists and sections as keys one day
    public static @NotNull String getNodeKey(@NotNull NodeTuple nodeTuple) {
        if (nodeTuple.getKeyNode() instanceof ScalarNode keyNode) {
            return keyNode.getValue();
        }
        return generateRandomKey();
    }

}
