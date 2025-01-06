package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public interface FieldKey {

    ConfigField getField(@NotNull Branch branch);

    Section createSection(@NotNull Branch branch);

    Sequence createSequence(@NotNull Branch branch);

    void set(@NotNull Branch branch, @NotNull Object value);

    void remove(@NotNull Branch branch);

    static List<FieldKey> keysOf(@NotNull String path) {
        List<FieldKey> keys = new ArrayList<>();
        final String[] rawKeys = path.toLowerCase().split("\\.");
        for (String rawKey : rawKeys) {
            final Matcher indicesMatcher = PathFormatter.INDEX_PATTERN.matcher(rawKey);
            if (indicesMatcher.matches()) {
                keys.add(IndexKey.fromString(indicesMatcher.group(1)));
                continue;
            }

            final String keyWithoutIndices = indicesMatcher.replaceAll("");
            final String[] aliases = keyWithoutIndices.split("\\|");

            keys.add(aliases.length > 1 ? new MultiKey(aliases) : new SimpleKey(keyWithoutIndices));

            indicesMatcher.reset();
            while (indicesMatcher.find()) {
                keys.add(IndexKey.fromString(indicesMatcher.group(1)));
            }
        }

        return keys;
    }

}