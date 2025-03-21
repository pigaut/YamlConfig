package io.github.pigaut.yaml;

import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.deserializer.*;
import io.github.pigaut.yaml.itemstack.*;
import io.github.pigaut.yaml.itemstack.attribute.*;
import io.github.pigaut.yaml.location.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;

public class SpigotConfigurator extends StandardConfigurator {

    public SpigotConfigurator() {
        this(true);
    }

    public SpigotConfigurator(boolean compact) {
        addMapper(ItemStack.class, new ItemStackMapper(compact));
        addMapper(Attribute.class, new AttributeMapper(compact));
        addMapper(Location.class, new LocationMapper(compact));

        addLoader(ItemStack.class, new ItemStackLoader());
        addLoader(Attribute.class, new AttributeLoader());
        addLoader(Location.class, new LocationLoader());

        addDeserializer(World.class, new WorldDeserializer());
        addDeserializer(Enchantment.class, new EnchantmentDeserializer());
        addDeserializer(Material.class, new MaterialDeserializer());
        addDeserializer(Particle.class, new ParticleDeserializer());
        addDeserializer(Sound.class, new SoundDeserializer());

        addSerializer(World.class, World::getName);
        addSerializer(Enchantment.class, enchant -> enchant.getKey().getKey());
    }

}
