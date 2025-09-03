package com.projectkorra.rpg.modules.leveling.gui;

import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.menu.Menu;
import com.projectkorra.rpg.ui.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainMenu implements Menu {
    private static final int ROWS = 3;

    @Override
    public InventoryUI buildUI(Player player) {
        ItemStack filler = ItemUtil.create(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack vines = ItemUtil.create(Material.TWISTING_VINES, " ");

        return InventoryUIBuilder.create(ROWS, "&1Main Menu")
                .fillLeftRight(vines, filler)
                .withButton(3, 1, ItemUtil.create(Material.NETHER_STAR, "&1Skill Tree"), click -> new SkillTreeMenu().open(player))
                .build();
    }
}
