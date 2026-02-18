package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class YamlConfig {

    private YamlConfig() {}

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
        final String fileName = file.getName();
        return file.isFile() && (fileName.endsWith(".yml") || fileName.endsWith(".yaml"));
    }

}
