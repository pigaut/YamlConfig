package io.github.pigaut.yamlib;

public class YAMLib {

    private YAMLib() {}

    public static String getFileName(String nameWithExtension) {
        int pos = nameWithExtension.lastIndexOf(".");
        return pos == -1 ? nameWithExtension : nameWithExtension.substring(0, pos);
    }

}
