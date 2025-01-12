package io.github.pigaut.yaml.itemstack;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.loader.*;
import io.github.pigaut.yaml.formatter.*;
import io.github.pigaut.yaml.itemstack.attribute.*;
import io.github.pigaut.yaml.parser.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.*;

import java.util.*;

/*
 / ItemStack config loader for 1.8.8+
*/
public class ItemStackLoader implements SectionLoader<ItemStack> {

    @Override
    public @NotNull String getProblemDescription() {
        return "Could not load itemstack";
    }

    @Override
    public @NotNull ItemStack loadFromSection(@NotNull ConfigSection config) throws InvalidConfigurationException {
        final Material type = config.get("type|material", Material.class);
        final int amount = config.getOptionalInteger("amount").orElse(1);
        final ItemStack item = new ItemStack(type, amount);
        {
            final ItemMeta meta = item.getItemMeta();

            final Optional<String> nameField = config.getOptionalString("name|display", StringColor.FORMATTER);
            nameField.ifPresent(meta::setDisplayName);

            final Optional<Integer> repairCostField = config.getOptionalInteger("repair-cost");
            repairCostField.ifPresent(repairCost -> {
                if (meta instanceof Repairable repairable) {
                    repairable.setRepairCost(repairCost);
                }
            });

            final List<String> lore = config.getStringList("lore", StringColor.FORMATTER);
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }

            final List<ItemFlag> itemFlags = config.getList("flags", ItemFlag.class);
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));

            final boolean shouldGlow = config.getOptionalBoolean("glow").orElse(false);
            if (shouldGlow) {
                meta.addEnchant(Enchantment.OXYGEN, 0, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

//            final PersistentData tata = meta.get

            item.setItemMeta(meta);
        }

        NBT.modify(item, itemNBT -> {
            final Optional<Integer> optionalModelData = config.getOptionalInteger("model-data|custom-model-data");
            optionalModelData.ifPresent(modelData -> itemNBT.setInteger("CustomModelData", modelData));

            final Optional<Integer> optionalDurability = config.getOptionalInteger("damage|durability");
            optionalDurability.ifPresent(durability -> itemNBT.setShort("Damage", durability.shortValue()));

            final Optional<Boolean> optionalUnbreakable = config.getOptionalBoolean("unbreakable");
            optionalUnbreakable.ifPresent(unbreakable -> itemNBT.setByte("Unbreakable", (byte) (unbreakable ? 1 : 0)));

            final Optional<String> optionalHeadData = config.getOptionalString("head-data|head");
            optionalHeadData.ifPresent(textureValue -> {
                final ReadWriteNBT skullOwnerCompound = itemNBT.getOrCreateCompound("SkullOwner");
                skullOwnerCompound.setUUID("Id", UUID.randomUUID());
                skullOwnerCompound.getOrCreateCompound("Properties")
                        .getCompoundList("textures")
                        .addCompound()
                        .setString("Value", textureValue);
            });

            final Optional<ConfigSection> optionalEnchants = config.getOptionalSection("enchants|enchantments");
            optionalEnchants.ifPresent(enchantsConfig -> {
                ReadWriteNBTCompoundList enchantmentCompounds = itemNBT.getCompoundList("Enchantments");
                for (String key : enchantsConfig.getKeys()) {
                    int level = enchantsConfig.getInteger(key);
                    ReadWriteNBT enchantmentCompound = enchantmentCompounds.addCompound();

                    enchantmentCompound.setString("id", StringFormatter.toKebabCase(key));
                    enchantmentCompound.setShort("lvl", (short) level);
                }
            });

            final ReadWriteNBTCompoundList attributeCompounds = itemNBT.getCompoundList("AttributeModifiers");
            for (Attribute attribute : config.getAll("attributes", Attribute.class)) {
                final ReadWriteNBT attributeCompound = attributeCompounds.addCompound();
                attributeCompound.setString("AttributeName", attribute.getAttributeType());
                attributeCompound.setString("Name", attribute.getName());
                attributeCompound.setDouble("Amount", attribute.getAmount());
                attributeCompound.setString("Slot", attribute.getSlot());
                attributeCompound.setInteger("Operation", attribute.getOperation());
                final UUID uuid = UUID.randomUUID();
                attributeCompound.setLong("UUIDLeast", uuid.getLeastSignificantBits());
                attributeCompound.setLong("UUIDMost", uuid.getMostSignificantBits());
            }
        });

        return item;
    }

}
