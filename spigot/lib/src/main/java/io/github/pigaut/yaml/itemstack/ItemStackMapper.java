package io.github.pigaut.yaml.itemstack;

import com.cryptomorin.xseries.*;
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

            if (meta instanceof Damageable damageable && damageable.hasDamage()) {
                config.set("damage", damageable.getDamage());
            }

            if (meta.isUnbreakable()) {
                config.set("unbreakable", true);
            }

            if (meta.hasCustomModelData()) {
                config.set("model-data", meta.getCustomModelData());
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

            final ConfigSection enchantsConfig = config.getSectionOrCreate("enchants");
            meta.getEnchants().forEach((enchant, level) -> {
                enchantsConfig.set(XEnchantment.of(enchant).name(), level);
            });

        }

        NBT.get(item, nbt -> {
            mapHeadTexture(config, nbt);
            mapAttributes(config, nbt);
        });
    }

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