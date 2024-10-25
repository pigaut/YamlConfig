package io.github.pigaut.yamlib.config.configurator.itemstack;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.*;
import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.config.configurator.*;
import io.github.pigaut.yamlib.util.*;
import org.bukkit.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.jetbrains.annotations.*;

import java.util.*;

/*
 / ItemStack config loader for 1.8.8+
*/
public class ItemStackLoader implements ConfigLoader<ItemStack> {

    @Override
    public @NotNull ItemStack load(@NotNull ConfigSection config) throws InvalidConfigurationException {
        Material type = config.get("type|material", Material.class);
        int amount = config.getOptionalInteger("amount").orElse(1);

        ItemStack item = new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();

        loadItemMeta(config, meta);
        item.setItemMeta(meta);

        NBT.modify(item, nbt -> {
            loadNBTData(config, nbt);
        });

        return item;
    }

    protected void loadItemMeta(ConfigSection config, ItemMeta meta) {
        Optional<String> nameField = config.getOptionalString("name|display", SpigotYAMLib.COLOR_FORMATTER);
        Optional<Integer> repairCostField = config.getOptionalInteger("repair-cost");
        List<String> lore = config.getStringList("lore", SpigotYAMLib.COLOR_FORMATTER);
        List<ItemFlag> itemFlags = config.getList("flags", ItemFlag.class);
        boolean shouldGlow = config.getOptionalBoolean("glow").orElse(false);

        nameField.ifPresent(meta::setDisplayName);
        meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));

        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        repairCostField.ifPresent(repairCost -> {
            if (meta instanceof Repairable repairable) {
                repairable.setRepairCost(repairCost);
            }
        });

        if (shouldGlow) {
            meta.addEnchant(Enchantment.OXYGEN, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
    }

    protected void loadNBTData(ConfigSection config, ReadWriteNBT itemNBT) {
        Optional<Integer> modelField = config.getOptionalInteger("custom-model");
        Optional<Integer> durabilityField = config.getOptionalInteger("damage|durability");
        Optional<Boolean> unbreakableField = config.getOptionalBoolean("unbreakable");
        Optional<String> headDataField = config.getOptionalString("head-data");
        Optional<ConfigSection> attributesField = config.getOptionalSection("attributes");
        Optional<ConfigSection> enchantsField = config.getOptionalSection("enchants|enchantments");

        modelField.ifPresent(modelData -> itemNBT.setInteger("CustomModelData", modelData));
        unbreakableField.ifPresent(unbreakable -> itemNBT.setByte("Unbreakable", (byte) (unbreakable ? 1 : 0)));
        durabilityField.ifPresent(durability -> itemNBT.setShort("Damage", durability.shortValue()));
        headDataField.ifPresent(textureValue -> loadHeadTexture(textureValue, itemNBT));
        attributesField.ifPresent(attributesConfig -> loadAttributes(attributesConfig, itemNBT));
        enchantsField.ifPresent(enchantsConfig -> loadEnchants(enchantsConfig, itemNBT));
    }

    protected void loadAttributes(ConfigSection attributesConfig, ReadWriteNBT itemNBT) {
        ReadWriteNBTCompoundList attributeCompounds = itemNBT.getCompoundList("AttributeModifiers");
        for (ConfigSection config : attributesConfig.getNestedSections()) {
            ReadWriteNBT attributeCompound = attributeCompounds.addCompound();

            String attribute = config.getString("type|attribute");
            String name = config.getOptionalString("name").orElse("");
            double amount = config.getDouble("amount");
            String slot = config.getString("slot");
            int operation = config.getInteger("operation");

            attributeCompound.setString("AttributeName", attribute);
            attributeCompound.setString("Name", name);
            attributeCompound.setDouble("Amount", amount);
            attributeCompound.setString("Slot", slot);
            attributeCompound.setInteger("Operation", operation);

            UUID uuid = UUID.randomUUID();
            attributeCompound.setLong("UUIDLeast", uuid.getLeastSignificantBits());
            attributeCompound.setLong("UUIDMost", uuid.getMostSignificantBits());
        }
    }

    protected void loadEnchants(ConfigSection enchantsConfig, ReadWriteNBT itemNBT) {
        enchantsConfig.isKeyedOrThrow();
        ReadWriteNBTCompoundList enchantmentCompounds = itemNBT.getCompoundList("Enchantments");
        for (String key : enchantsConfig.getKeys()) {
            int level = enchantsConfig.getInteger(key);
            ReadWriteNBT enchantmentCompound = enchantmentCompounds.addCompound();

            enchantmentCompound.setString("id", StringFormatter.NAMESPACE.format(key));
            enchantmentCompound.setShort("lvl", (short) level);
        }
    }

    protected void loadHeadTexture(String textureValue, ReadWriteNBT itemNBT) {
        final ReadWriteNBT skullOwnerCompound = itemNBT.getOrCreateCompound("SkullOwner");
        skullOwnerCompound.setUUID("Id", UUID.randomUUID());
        skullOwnerCompound.getOrCreateCompound("Properties")
                .getCompoundList("textures")
                .addCompound()
                .setString("Value", textureValue);
    }

}
