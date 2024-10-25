package io.github.pigaut.yamlib.config.parser;

import org.bukkit.*;

public class SpigotParser extends StandardParser {

    public SpigotParser() {
        WorldSerializable worldSerializable = new WorldSerializable();
        registerDeserializer(World.class, worldSerializable);
        registerSerializer(World.class, worldSerializable);

    }

}
