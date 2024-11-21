package io.github.pigaut.yamlib.config.configurator;

import io.github.pigaut.yamlib.config.configurator.itemstack.*;
import io.github.pigaut.yamlib.config.configurator.location.*;
import io.github.pigaut.yamlib.config.configurator.world.*;
import io.github.pigaut.yamlib.configurator.StandardConfigurator;
import io.github.pigaut.yamlib.configurator.field.*;
import io.github.pigaut.yamlib.parser.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.*;

public class SpigotConfigurator extends StandardConfigurator {

    public SpigotConfigurator() {
        this(true);
    }

    public SpigotConfigurator(boolean compact) {
        this(compact, Bukkit.getWorlds().get(0));
    }

    public SpigotConfigurator(boolean compact, World defaultWorld) {
        addMapper(ItemStack.class, new ItemStackMapper(compact));
        addMapper(Location.class, new LocationMapper(compact));

        addLoader(ItemStack.class, new ItemStackLoader());
        addLoader(Location.class, new LocationLoader(defaultWorld));

        addDeserializer(World.class, new WorldDeserializer());
        addSerializer(World.class, World::getName);
    }

}
