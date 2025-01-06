package io.github.pigaut.yaml;

import org.jetbrains.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

public class YamlConfig {

    private YamlConfig() {}

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
