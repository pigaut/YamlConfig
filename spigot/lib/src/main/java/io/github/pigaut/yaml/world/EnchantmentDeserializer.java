package io.github.pigaut.yaml.world;

import io.github.pigaut.yaml.configurator.parser.*;
import io.github.pigaut.yaml.parser.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.jetbrains.annotations.*;

public class EnchantmentDeserializer implements ConfigDeserializer<Enchantment> {

    @Override
    public @NotNull Enchantment deserialize(@NotNull String enchantName) throws DeserializationException {
        final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(StringFormatter.toSnakeCase(enchantName)));
        if (enchantment == null) {
            throw new DeserializationException(enchantName + " is not a valid enchant");
        }
        return enchantment;
    }

}
