package com.projectkorra.rpg.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface InventoryUI extends InventoryHolder {
    void open(final @NotNull Player player);

    default void handleClick(final InventoryClickEvent event) {}

    // Close callback
    default void onClose(final @NotNull Player player) {}

    @Override @NotNull Inventory getInventory();
}
