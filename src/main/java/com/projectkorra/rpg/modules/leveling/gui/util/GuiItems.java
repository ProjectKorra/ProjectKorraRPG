package com.projectkorra.rpg.modules.leveling.gui.util;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiItems {
    public static GuiItem glassPaneItem() {
        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassPaneMeta = glassPane.getItemMeta();
        assert glassPaneMeta != null;
        glassPaneMeta.setDisplayName(" ");
        glassPane.setItemMeta(glassPaneMeta);
        return new GuiItem(glassPane);
    }

    public static GuiItem twistedVinesItem() {
        ItemStack vine = new ItemStack(Material.TWISTING_VINES);
        ItemMeta vineMeta = vine.getItemMeta();
        assert vineMeta != null;
        vineMeta.setDisplayName(" ");
        vine.setItemMeta(vineMeta);
        return new GuiItem(vine);
    }
}
