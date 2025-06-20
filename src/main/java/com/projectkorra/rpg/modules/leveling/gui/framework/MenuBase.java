package com.projectkorra.rpg.modules.leveling.gui.framework;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class MenuBase implements InventoryHolder {
    protected Map<Integer, MenuItem> items = new HashMap<>();
    protected Inventory inventory;
    protected String title;
    protected int size;
    protected int lastClickedSlot = -1;

    public MenuBase(String title, int rows) {
        this.title = title;
        this.size = rows * 9;
    }

    public boolean addMenuItem(MenuItem item, int x, int y) {
        return addMenuItem(item, y * 9 + x);
    }

    public boolean addMenuItem(MenuItem item, int index) {
        if (index < 0) {
            index = getInventory().getSize() - index;
        }

        ItemStack slot = getInventory().getItem(index);
        if (slot != null && slot.getType() != Material.AIR) {
            return false;
        }

        ItemStack stack = item.getItem();

        if (!item.getItem().getEnchantments().isEmpty()) {
            // ADD GLOW
        }

        getInventory().setItem(index, stack);
        items.put(index, item);
        //item.setMenu(this);
        return true;
    }
}
