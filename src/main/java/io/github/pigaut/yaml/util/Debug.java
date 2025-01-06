package io.github.pigaut.yaml.util;

import java.util.logging.*;

public class Debug {

    private static int count = 1;

    public static void send(Object value) {
        Logger.getLogger("YAML").severe("'" + count++ + "' = " + value);
    }

}
