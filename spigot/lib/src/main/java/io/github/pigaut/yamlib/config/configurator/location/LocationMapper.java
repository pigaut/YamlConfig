package io.github.pigaut.yamlib.config.configurator.location;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.configurator.section.ConfigMapper;
import io.github.pigaut.yamlib.snakeyaml.engine.v2.common.*;
import org.bukkit.*;

public class LocationMapper implements ConfigMapper<Location> {

    private final boolean compact;

    public LocationMapper(boolean compact) {
        this.compact = compact;
    }

    @Override
    public void map(ConfigSection config, Location location) {
        World world = location.getWorld();

        if (world != null) {
            config.set("world", world.getName());
        }

        config.set("x", location.getX());
        config.set("y", location.getY());
        config.set("z", location.getZ());

        float yaw = location.getYaw();
        if (yaw != 0) {
            config.set("yaw", yaw);
        }

        double pitch = location.getPitch();
        if (pitch != 0) {
            config.set("pitch", pitch);
        }

        config.setKeyless(false);
        if (compact) {
            config.setFlowStyle(FlowStyle.FLOW);
        }
    }

}
