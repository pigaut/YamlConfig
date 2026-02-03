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
    public static RootSection loadSection(File file) throws ConfigurationLoadException {
        return loadSection(file, new StandardConfigurator(), (String) null);
    }

    @NotNull
    public static RootSection loadSection(File file, Consumer<ConfigurationLoadException> errorCollector) {
        return loadSection(file, new StandardConfigurator(), null, errorCollector);
    }

    @NotNull
    public static RootSection loadSection(File file, Configurator configurator) throws ConfigurationLoadException {
        return loadSection(file, configurator, (String) null);
    }

   @NotNull
    public static RootSection loadSection(File file, Configurator configurator, Consumer<ConfigurationLoadException> errorCollector) {
        return loadSection(file, configurator, null, errorCollector);
    }

    @NotNull
    public static RootSection loadSection(File file, Configurator configurator, String prefix) throws ConfigurationLoadException {
        RootSection section = createEmptySection(file, configurator, prefix);
        section.load();
        return section;
    }

    @NotNull
    public static RootSection loadSection(File file, Configurator configurator, String prefix, Consumer<ConfigurationLoadException> errorCollector) {
        RootSection section = createEmptySection(file, configurator, prefix);
        try {
            section.load();
        }
        catch (ConfigurationLoadException e) {
            errorCollector.accept(e);
        }
        return section;
    }

    @NotNull
    public static RootSection createEmptySection() {
        return new RootSection(null, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSection createEmptySection(File file) {
        return new RootSection(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSection createEmptySection(File file, Configurator configurator) {
        return new RootSection(file, configurator, null);
    }

    @NotNull
    public static RootSection createEmptySection(File file, Configurator configurator, String prefix) {
        return new RootSection(file, configurator, prefix);
    }

    @NotNull
    public static RootSequence loadSequence(File file) throws ConfigurationLoadException {
        return loadSequence(file, new StandardConfigurator(), (String) null);
    }

    @NotNull
    public static RootSequence loadSequence(File file, Consumer<ConfigurationLoadException> errorCollector) {
        return loadSequence(file, new StandardConfigurator(), null, errorCollector);
    }

    @NotNull
    public static RootSequence loadSequence(File file, Configurator configurator) throws ConfigurationLoadException {
        return loadSequence(file, configurator, (String) null);
    }

    @NotNull
    public static RootSequence loadSequence(File file, Configurator configurator, Consumer<ConfigurationLoadException> errorCollector) {
        return loadSequence(file, configurator, null, errorCollector);
    }

    @NotNull
    public static RootSequence loadSequence(File file, Configurator configurator, String prefix) throws ConfigurationLoadException {
        RootSequence sequence = createEmptySequence(file, configurator, prefix);
        sequence.load();
        return sequence;
    }

    @NotNull
    public static RootSequence loadSequence(File file, Configurator configurator, String prefix, Consumer<ConfigurationLoadException> errorCollector) {
        RootSequence sequence = createEmptySequence(file, configurator, prefix);
        try {
            sequence.load();
        }
        catch (ConfigurationLoadException e) {
            errorCollector.accept(e);
        }
        return sequence;
    }

    @NotNull
    public static RootSequence createEmptySequence() {
        return new RootSequence(null, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSequence createEmptySequence(File file) {
        return new RootSequence(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootSequence createEmptySequence(File file, Configurator configurator) {
        return new RootSequence(file, configurator, null);
    }

    @NotNull
    public static RootSequence createEmptySequence(File file, Configurator configurator, String prefix) {
        return new RootSequence(file, configurator, prefix);
    }

    @NotNull
    public static RootScalar loadScalar(File file) throws ConfigurationLoadException {
        return loadScalar(file, new StandardConfigurator(), (String) null);
    }

    @NotNull
    public static RootScalar loadScalar(File file, Consumer<ConfigurationLoadException> errorCollector) {
        return loadScalar(file, new StandardConfigurator(), null, errorCollector);
    }

    @NotNull
    public static RootScalar loadScalar(File file, Configurator configurator) throws ConfigurationLoadException {
        return loadScalar(file, configurator, (String) null);
    }

    @NotNull
    public static RootScalar loadScalar(File file, Configurator configurator, Consumer<ConfigurationLoadException> errorCollector) {
        return loadScalar(file, configurator, null, errorCollector);
    }

    @NotNull
    public static RootScalar loadScalar(File file, Configurator configurator, String prefix) throws ConfigurationLoadException {
        RootScalar scalar = createEmptyScalar(file, configurator, prefix);
        scalar.load();
        return scalar;
    }

    @NotNull
    public static RootScalar loadScalar(File file, Configurator configurator, String prefix, Consumer<ConfigurationLoadException> errorCollector) {
        RootScalar scalar = createEmptyScalar(file, configurator, prefix);
        try {
            scalar.load();
        } catch (ConfigurationLoadException e) {
            errorCollector.accept(e);
        }
        return scalar;
    }

    @NotNull
    public static RootScalar createEmptyScalar() {
        return new RootScalar(null, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootScalar createEmptyScalar(File file) {
        return new RootScalar(file, new StandardConfigurator(), null);
    }

    @NotNull
    public static RootScalar createEmptyScalar(File file, Configurator configurator) {
        return new RootScalar(file, configurator, null);
    }

    @NotNull
    public static RootScalar createEmptyScalar(File file, Configurator configurator, String prefix) {
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
