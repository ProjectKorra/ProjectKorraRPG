package com.projectkorra.rpg.modules.leveling.gui.framework;

import org.bukkit.inventory.ItemStack;

public class MenuItem {
    private final ItemStack item;
    private final String name;
    private final Runnable runnable;

    public MenuItem(ItemStack item, String name, Runnable runnable) {
        this.item = item;
        this.name = name;
        this.runnable = runnable;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
