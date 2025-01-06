package io.github.pigaut.yaml.itemstack;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.*;
import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.mapper.*;
import io.github.pigaut.yaml.itemstack.attribute.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

/*
 / ItemStack config mapper for 1.8.8+
*/
public class ItemStackMapper implements SectionMapper<ItemStack> {

    private final boolean compact;

    public ItemStackMapper(boolean compact) {
        this.compact = compact;
    }

    @Override
    public void mapSection(ConfigSection config, ItemStack item) {
        Material type = item.getType();
        config.set("type", type);
        int amount = item.getAmount();
        if (amount != 1) {
            config.set("amount", item.getAmount());
        }

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta.hasDisplayName()) {
                config.set("name", meta.getDisplayName());
            }

            if (meta.hasLore()) {
                config.set("lore", meta.getLore());
            }

            if (meta instanceof Repairable repairable) {
                if (repairable.hasRepairCost()) {
                    config.set("repair-cost", repairable.getRepairCost());
                }
            }

            Set<ItemFlag> itemFlags = meta.getItemFlags();
            if (!itemFlags.isEmpty()) {
                config.set("flags", itemFlags);
            }
        }

        NBT.get(item, nbt -> {
            mapNBTData(config, nbt);

            Boolean unbreakable = nbt.getBoolean("Unbreakable");
            if (unbreakable != null && unbreakable) {
                config.set("unbreakable", true);
            }

            Short damage = nbt.getShort("Damage");
            if (damage != 0) {
                config.set("damage", damage);
            }

            Integer modelData = nbt.getInteger("CustomModelData");
            if (modelData != 0) {
                config.set("model-data", modelData);
            }

            mapHeadTexture(config, nbt);
            mapEnchants(config, nbt);
            mapAttributes(config, nbt);
        });
    }

    protected void mapNBTData(ConfigSection config, ReadableNBT itemNBT) {}

    protected void mapAttributes(ConfigSection section, ReadableNBT itemNBT) {
        final ReadableNBTList<ReadWriteNBT> attributesCompound = itemNBT.getCompoundList("AttributeModifiers");
        final List<Attribute> foundAttributes = new ArrayList<>();
        if (attributesCompound != null) {
            for (ReadWriteNBT attributeCompound : attributesCompound) {
                final String attribute = attributeCompound.getString("AttributeName")
                        .replace("minecraft:", "");
                final String name = attributeCompound.getString("Name");
                final double amount = attributeCompound.getDouble("Amount");
                final String slot = attributeCompound.getString("Slot");
                final int operation = attributeCompound.getInteger("Operation");

                foundAttributes.add(new Attribute(attribute, name, amount, slot, operation));
            }
        }

        if (!foundAttributes.isEmpty()) {
            section.set("attributes", foundAttributes);
        }
    }

    protected void mapEnchants(ConfigSection config, ReadableNBT itemNBT) {
        ReadableNBTList<ReadWriteNBT> enchantsList = itemNBT.getCompoundList("Enchantments");

        if (enchantsList != null) {
            ConfigSection enchantsConfig = config.getSectionOrCreate("enchants");
            for (ReadWriteNBT enchantCompound : enchantsList) {
                enchantsConfig.set(
                        enchantCompound.getString("id").replace("minecraft:", ""),
                        enchantCompound.getShort("lvl")
                );
            }
            if (enchantsConfig.isEmpty()) {
                config.remove("enchants");
            }
        }
    }

    protected void mapHeadTexture(ConfigSection config, ReadableNBT itemNBT) {
        final ReadableNBT skullOwnerCompound = itemNBT.getCompound("SkullOwner");
        if (skullOwnerCompound == null) return;

        final ReadableNBT propertiesCompound = skullOwnerCompound.getCompound("Properties");
        if (propertiesCompound == null) return;

        ReadableNBTList<ReadWriteNBT> texturesList = propertiesCompound.getCompoundList("textures");
        if (texturesList == null) return;

        for (ReadWriteNBT texture : texturesList) {
            String value = texture.getString("Value");
            if (value != null) {
                config.set("head-data", value);
                return;
            }
        }
    }

}