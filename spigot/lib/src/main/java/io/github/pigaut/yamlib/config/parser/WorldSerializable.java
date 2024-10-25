package io.github.pigaut.yamlib.config.parser;

import io.github.pigaut.yamlib.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class WorldSerializable implements Deserializer<World>, Serializer<World> {

    @Override
    public @NotNull World deserialize(@NotNull String worldName) throws DeserializationException {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new DeserializationException("world not found");
        }

        return world;
    }

    @Override
    public @NotNull String serialize(@NotNull World world) {
        return world.getName();
    }

}
