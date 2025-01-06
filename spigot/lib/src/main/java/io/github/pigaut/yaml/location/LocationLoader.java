package io.github.pigaut.yaml.location;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.loader.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class LocationLoader implements ConfigLoader<Location> {

    @Override
    public @NotNull String getProblemDescription() {
        return "Could not load location";
    }

    @Override
    public @NotNull Location loadFromSection(@NotNull ConfigSection config) throws InvalidConfigurationException {
        World world = config.getOptional("world", World.class).orElse(null);
        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");
        float yaw = config.getOptionalFloat("yaw").orElse(0f);
        float pitch = config.getOptionalFloat("pitch").orElse(0f);

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public @NotNull Location loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
        switch (sequence.size()) {
            case 3 -> {
                double x = sequence.getDouble(0);
                double y = sequence.getDouble(1);
                double z = sequence.getDouble(2);
                return new Location(null, x, y, z);
            }
            case 4 -> {
                World world = sequence.get(0, World.class);
                double x = sequence.getDouble(1);
                double y = sequence.getDouble(2);
                double z = sequence.getDouble(3);
                return new Location(world, x, y, z);
            }
            case 5 -> {
                double x = sequence.getDouble(0);
                double y = sequence.getDouble(1);
                double z = sequence.getDouble(2);
                float yaw = sequence.getFloat(3);
                float pitch = sequence.getFloat(4);
                return new Location(null, x, y, z, yaw, pitch);
            }
            case 6 -> {
                World world = sequence.get(0, World.class);
                double x = sequence.getDouble(1);
                double y = sequence.getDouble(2);
                double z = sequence.getDouble(3);
                float yaw = sequence.getFloat(4);
                float pitch = sequence.getFloat(5);
                return new Location(world, x, y, z, yaw, pitch);
            }
            default -> throw new InvalidConfigurationException(sequence, "[(world), <x>, <y>, <z>, (yaw), (pitch)]");
        }
    }

}
