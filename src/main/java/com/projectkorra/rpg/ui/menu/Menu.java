package com.projectkorra.rpg.ui.menu;

import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.ui.InventoryUI;
import org.bukkit.entity.Player;

public interface Menu {
    InventoryUI buildUI(Player player);

    default void open(Player player) {
        InventoryUI ui = buildUI(player);
        ProjectKorraRPG.getPlugin().getInventoryService().open(player, ui);
    }
}
