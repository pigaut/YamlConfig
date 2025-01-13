package io.github.pigaut.yaml.deserializer;

import io.github.pigaut.yaml.configurator.parser.*;
import io.github.pigaut.yaml.parser.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class WorldDeserializer implements ConfigDeserializer<World> {

    @Override
    public @NotNull World deserialize(@NotNull String worldName) throws DeserializationException {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new DeserializationException(worldName + " is not a valid world");
        }

        return world;
    }

}
