package io.github.pigaut.yamlib.config.configurator.location;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.configurator.section.ConfigLoader;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class LocationLoader implements ConfigLoader<Location> {

    private final World defaultWorld;

    public LocationLoader(@Nullable World defaultWorld) {
        this.defaultWorld = defaultWorld;
    }

    @Override
    public @NotNull Location load(@NotNull ConfigSection config) throws InvalidConfigurationException {
        World world = config.getOptional("world", World.class).orElse(defaultWorld);
        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");
        float yaw = config.getOptionalFloat("yaw").orElse(0f);
        float pitch = config.getOptionalFloat("pitch").orElse(0f);

        return new Location(world, x, y, z, yaw, pitch);
    }

}
