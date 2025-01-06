package io.github.pigaut.yaml.location;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.mapper.*;
import io.github.pigaut.yaml.snakeyaml.engine.v2.common.*;
import org.bukkit.*;
import org.jetbrains.annotations.*;

public class LocationMapper implements ConfigMapper<Location> {

    private final boolean compact;

    public LocationMapper(boolean compact) {
        this.compact = compact;
    }

    @Override
    public @NotNull MappingType getDefaultMappingType() {
        return MappingType.SEQUENCE;
    }

    @Override
    public void mapSequence(@NotNull ConfigSequence sequence, @NotNull Location location) {
        final World world = location.getWorld();
        if (world != null) {
            sequence.add(world.getName());
        }

        sequence.add(location.getX());
        sequence.add(location.getY());
        sequence.add(location.getZ());

        final float yaw = location.getYaw();
        final double pitch = location.getPitch();

        if (yaw != 0 || pitch != 0) {
            sequence.add(yaw);
            sequence.add(pitch);
        }

        if (compact) {
            sequence.setFlowStyle(FlowStyle.FLOW);
        }
    }

    @Override
    public void mapSection(ConfigSection section, Location location) {
        World world = location.getWorld();

        if (world != null) {
            section.set("world", world.getName());
        }

        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());

        float yaw = location.getYaw();
        if (yaw != 0) {
            section.set("yaw", yaw);
        }

        double pitch = location.getPitch();
        if (pitch != 0) {
            section.set("pitch", pitch);
        }

        if (compact) {
            section.setFlowStyle(FlowStyle.FLOW);
        }
    }

}
