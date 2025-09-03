package com.projectkorra.rpg.ui.impl;

import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.Slot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class BasicInventoryUI implements InventoryUI {
    private final Map<Slot, Consumer<InventoryClickEvent>> clickHandlers;
    private final Inventory inventory;

    public BasicInventoryUI(int rows, String title, Map<Slot, ItemStack> items, Map<Slot, Consumer<InventoryClickEvent>> clickHandlers) {
        this.clickHandlers = Map.copyOf(clickHandlers);
        this.inventory = Bukkit.createInventory(this, rows * 9, title);

        items.forEach((slot, stack) -> inventory.setItem(slot.index(), stack));
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Slot slot = Slot.of(event.getRawSlot() % 9, event.getRawSlot() / 9);
        Consumer<InventoryClickEvent> action = clickHandlers.get(slot);
        if (action != null) action.accept(event);
        event.setCancelled(true);
    }
}
