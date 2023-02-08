package com.nekozouneko.nekojosen.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemStackBuilder {

    protected final ItemStack stack;
    private final ItemMeta meta;

    public ItemStackBuilder(Material material) {
        Objects.requireNonNull(material);
        if (material.isAir()) throw new IllegalArgumentException("Argument 'material' is air material.");
        stack = new ItemStack(material);
        meta = stack.getItemMeta();
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        Scoreboard sb = null;
        return stack;
    }

    public ItemStackBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemStackBuilder name(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemStackBuilder lore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemStackBuilder lore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemStackBuilder customModelData(Integer i) {
        meta.setCustomModelData(i);
        return this;
    }

    public ItemStackBuilder unbreakable(boolean bool) {
        meta.setUnbreakable(bool);
        return this;
    }

    public ItemStackBuilder attribute(Attribute attribute, AttributeModifier modifier) {
        meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemStackBuilder enchant(Enchantment ench, int level, boolean bypass) {
        meta.addEnchant(ench, level, bypass);
        return this;
    }

    public ItemStackBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public <Z> ItemStackBuilder bukkitData(NamespacedKey key, PersistentDataType<?, Z> type, Z value) {
        meta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

}
