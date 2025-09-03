package com.projectkorra.rpg.ui.service;

import com.projectkorra.rpg.ui.InventoryUI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of open UIs and provides open / close methods.
 */
public class InventoryService {
    private final Map<Player, InventoryUI> openUis = new HashMap<>();

    public void open(Player player, InventoryUI ui) {
        InventoryUI old = openUis.put(player, ui);

        // Close any previously open menus
        if (old != null && old != ui) {
            old.onClose(player);
        }

        ui.open(player);
    }

    public InventoryUI get(Player player) {
        return openUis.get(player);
    }

    public void close(Player player) {
        InventoryUI old = openUis.remove(player);
        if (old != null) {
            old.onClose(player);
        }
    }

    public void closeAll() {
        openUis.forEach((p, ui) -> ui.onClose(p));
        openUis.clear();
    }

    public Map<Player, InventoryUI> getOpenUis() {
        return openUis;
    }
}
