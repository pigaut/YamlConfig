package io.github.pigaut.yamlib.config.configurator.itemstack;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.*;
import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.configurator.section.ConfigMapper;
import io.github.pigaut.yamlib.snakeyaml.engine.v2.common.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

/*
 / ItemStack config mapper for 1.8.8+
*/
public class ItemStackMapper implements ConfigMapper<ItemStack> {

    private final boolean compact;

    public ItemStackMapper(boolean compact) {
        this.compact = compact;
    }

    @Override
    public void map(ConfigSection config, ItemStack item) {
        Material type = item.getType();
        config.set("type", type);
        int amount = item.getAmount();
        if (amount != 1) {
            config.set("amount", item.getAmount());
        }

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

        NBT.get(item, nbt -> {
            mapNBTData(config, nbt);

            Boolean unbreakable = nbt.getBoolean("Unbreakable");
            if (unbreakable != null && unbreakable) {
                config.set("unbreakable", true);
            }

            Short damage = nbt.getShort("Damage");
            if (damage != null) {
                config.set("damage", damage);
            }

            Integer modelData = nbt.getInteger("CustomModelData");
            if (modelData != null) {
                config.set("model-data", modelData);
            }

            mapHeadTexture(config, nbt);
            mapEnchants(config, nbt);
            mapAttributes(config, nbt);
        });
    }

    protected void mapNBTData(ConfigSection config, ReadableNBT itemNBT) {}

    protected void mapAttributes(ConfigSection config, ReadableNBT itemNBT) {
        ReadableNBTList<ReadWriteNBT> attributesList = itemNBT.getCompoundList("AttributeModifiers");

        if (attributesList != null) {
            ConfigSection attributesConfig = config.getSectionOrCreate("attributes");
            attributesConfig.clear();

            if (compact) {
                attributesConfig.setKeyless(true);
                attributesConfig.setDefaultFlowStyle(FlowStyle.FLOW);
            }

            for (ReadWriteNBT attributeCompound : attributesList) {
                ConfigSection attributeConfig = attributesConfig.addSection();

                String attributeName = attributeCompound.getString("AttributeName");
                String name = attributeCompound.getString("Name");
                double attributeAmount = attributeCompound.getDouble("Amount");
                String slot = attributeCompound.getString("Slot");
                int operation = attributeCompound.getInteger("Operation");

                attributeConfig.set("attribute", attributeName.replace("minecraft:", ""));
                if (!name.isEmpty()) {
                    attributeConfig.set("name", name);
                }
                attributeConfig.set("amount", attributeAmount);
                attributeConfig.set("slot", slot);
                attributeConfig.set("operation", operation);
            }
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