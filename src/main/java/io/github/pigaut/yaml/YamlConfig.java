package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

public class YamlConfig {

    private YamlConfig() {}

    @NotNull
    public static RootSection loadSection(File file) {
        return loadSection(file, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootSection loadSection(File file, String prefix) {
        return loadSection(file, new StandardConfigurator(), prefix, true);
    }

    @NotNull
    public static RootSection loadSection(File file, @NotNull Configurator configurator) {
        return loadSection(file, configurator, null, true);
    }

    @NotNull
    public static RootSection loadSection(File file, @NotNull Configurator configurator, String prefix) {
        return loadSection(file, configurator, prefix, true);
    }

    @NotNull
    public static RootSection loadSection(File file, @NotNull Configurator configurator, String prefix, boolean debug) {
        RootSection rootSection = createEmptySection(file, configurator, prefix, debug);
        rootSection.load();
        return rootSection;
    }

    @NotNull
    public static RootSection createEmptySection() {
        return new RootSection(null, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootSection createEmptySection(File file) {
        return new RootSection(file, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootSection createEmptySection(File file, @NotNull Configurator configurator) {
        return new RootSection(file, configurator, null, true);
    }

    @NotNull
    public static RootSection createEmptySection(File file, @NotNull Configurator configurator, String prefix) {
        return new RootSection(file, configurator, prefix, true);
    }

    @NotNull
    public static RootSection createEmptySection(File file, @NotNull Configurator configurator, String prefix, boolean debug) {
        return new RootSection(file, configurator, prefix, debug);
    }

    @NotNull
    public static RootSequence loadSequence(File file) {
        return loadSequence(file, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootSequence loadSequence(File file, String prefix) {
        return loadSequence(file, new StandardConfigurator(), prefix, true);
    }

    @NotNull
    public static RootSequence loadSequence(File file, @NotNull Configurator configurator) {
        return loadSequence(file, configurator, null, true);
    }

    @NotNull
    public static RootSequence loadSequence(File file, @NotNull Configurator configurator, String prefix) {
        return loadSequence(file, configurator, prefix, true);
    }

    @NotNull
    public static RootSequence loadSequence(File file, @NotNull Configurator configurator, String prefix, boolean debug) {
        RootSequence rootSequence = createEmptySequence(file, configurator, prefix, debug);
        rootSequence.load();
        return rootSequence;
    }

    @NotNull
    public static RootSequence createEmptySequence() {
        return new RootSequence(null, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootSequence createEmptySequence(File file) {
        return new RootSequence(file, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootSequence createEmptySequence(File file, @NotNull Configurator configurator) {
        return new RootSequence(file, configurator, null, true);
    }

    @NotNull
    public static RootSequence createEmptySequence(File file, @NotNull Configurator configurator, String prefix) {
        return new RootSequence(file, configurator, prefix, true);
    }

    @NotNull
    public static RootSequence createEmptySequence(File file, @NotNull Configurator configurator, String prefix, boolean debug) {
        return new RootSequence(file, configurator, prefix, debug);
    }

    @NotNull
    public static RootScalar loadScalar(File file) {
        return loadScalar(file, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootScalar loadScalar(File file, String prefix) {
        return loadScalar(file, new StandardConfigurator(), prefix, true);
    }

    @NotNull
    public static RootScalar loadScalar(File file, @NotNull Configurator configurator) {
        return loadScalar(file, configurator, null, true);
    }

    @NotNull
    public static RootScalar loadScalar(File file, @NotNull Configurator configurator, String prefix) {
        return loadScalar(file, configurator, prefix, true);
    }

    @NotNull
    public static RootScalar loadScalar(File file, @NotNull Configurator configurator, String prefix, boolean debug) {
        RootScalar rootScalar = createEmptyScalar(file, configurator, prefix, debug);
        rootScalar.load();
        return rootScalar;
    }

    @NotNull
    public static RootScalar createEmptyScalar() {
        return new RootScalar(null, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootScalar createEmptyScalar(File file) {
        return new RootScalar(file, new StandardConfigurator(), null, true);
    }

    @NotNull
    public static RootScalar createEmptyScalar(File file, @NotNull Configurator configurator) {
        return new RootScalar(file, configurator, null, true);
    }

    @NotNull
    public static RootScalar createEmptyScalar(File file, @NotNull Configurator configurator, String prefix) {
        return new RootScalar(file, configurator, prefix, true);
    }

    @NotNull
    public static RootScalar createEmptyScalar(File file, @NotNull Configurator configurator, String prefix, boolean debug) {
        return new RootScalar(file, configurator, prefix, debug);
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
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean isYamlFile(@NotNull File file) {
        final String fileName = file.getName();
        return file.isFile() && (fileName.endsWith(".yml") || fileName.endsWith(".yaml"));
    }

}
