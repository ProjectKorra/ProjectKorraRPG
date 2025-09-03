package com.projectkorra.rpg.modules.leveling.gui;

import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.menu.Menu;
import org.bukkit.entity.Player;

public class SkillTreeMenu implements Menu {
    private static final int ROWS = 3;

    @Override
    public InventoryUI buildUI(Player player) {
        return InventoryUIBuilder.create(ROWS, "&6Skill Tree").build();
    }
}
