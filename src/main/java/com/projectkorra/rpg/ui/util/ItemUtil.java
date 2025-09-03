package com.projectkorra.rpg.ui.util;

import com.projectkorra.projectkorra.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public final class ItemUtil {
    private ItemUtil() {}

    public static ItemStack create(Material material, String name) {
        return create(material, name, List.of());
    }

    public static ItemStack create(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta(), () -> "Cannot get ItemMeta for " + material);
        meta.setDisplayName(ChatUtil.color(name));

        if (!lore.isEmpty()) {
            List<String> coloredLore = lore.stream().map(ChatUtil::color).toList();
            meta.setLore(coloredLore);
        }

        item.setItemMeta(meta);
        return item;
    }
}
