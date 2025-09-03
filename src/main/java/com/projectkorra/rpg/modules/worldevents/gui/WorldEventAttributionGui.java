package com.projectkorra.rpg.modules.worldevents.gui;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.worldevents.models.WorldEvent;
import com.projectkorra.rpg.ui.InventoryUI;
import com.projectkorra.rpg.ui.builder.InventoryUIBuilder;
import com.projectkorra.rpg.ui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WorldEventAttributionGui implements Menu {
    private static final int ROWS = 3;
    private final WorldEvent worldEvent;

    public WorldEventAttributionGui(WorldEvent worldEvent) {
        this.worldEvent = worldEvent;
    }

    @Override
    public InventoryUI buildUI(Player player) {
        return InventoryUIBuilder.create(ROWS, ChatUtil.color(worldEvent.getTitle()))
                .fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                .withItem(0, 0, new ItemStack(Material.BLAZE_POWDER))
                .build();
    }

    public WorldEvent getWorldEvent() {
        return worldEvent;
    }
}
