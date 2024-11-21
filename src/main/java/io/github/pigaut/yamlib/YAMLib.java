package io.github.pigaut.yamlib;

import java.math.*;
import java.util.*;

public class YAMLib {

    private YAMLib() {}

    public static final List<Class<?>> SCALARS = List.of(Boolean.class, Character.class, String.class, Byte.class, Short.class, Integer.class,
            Long.class, Float.class, Double.class, BigInteger.class, BigDecimal.class);

    public static String getFileName(String nameWithExtension) {
        int pos = nameWithExtension.lastIndexOf(".");
        return pos == -1 ? nameWithExtension : nameWithExtension.substring(0, pos);
    }

}
