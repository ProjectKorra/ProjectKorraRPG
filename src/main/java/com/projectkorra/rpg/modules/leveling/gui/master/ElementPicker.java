package com.projectkorra.rpg.modules.leveling.gui.master;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.leveling.gui.util.GuiItems;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ElementPicker extends ChestGui {
    private static final int WIDTH = 9;
    private static final int HEIGHT = 3;

    private final Player player;
    private final StaticPane background;
    private final List<Element> elements;

    public ElementPicker(Player player) {
        super(HEIGHT, ChatUtil.color("Select Element"));
        this.player = player;
        this.background = new StaticPane(0, 0, WIDTH, HEIGHT);
        this.elements = BendingPlayer.getBendingPlayer(player).getElements();

        setOnGlobalClick(event -> event.setCancelled(true));

        addPane(background);
        background.setVisible(true);

        setupGui();
    }

    private void setupGui() {
        // fill the background with gray glass
        background.fillWith(GuiItems.glassPaneItem().getItem());

        // draw twisted vines border
        GuiItem vines = GuiItems.twistedVinesItem();
        // top and bottom rows
        for (int x = 1; x < WIDTH; x++) {
            background.addItem(vines, x, 0);
            background.addItem(vines, x, HEIGHT - 1);
        }
        // left and right columns
        for (int y = 0; y < HEIGHT; y++) {
            background.addItem(vines, 0, y);
            background.addItem(vines, WIDTH - 1, y);
        }

        // collect and sort element items
        List<ItemStack> items = elementItems();

        // place element buttons starting at (1,1)
        int paneWidth = WIDTH - 2;
        int x = 0, y = 0;
        for (ItemStack stack : items) {

            GuiItem guiItem = new GuiItem(stack, event -> {
                event.setCancelled(true);
                ItemStack clicked = event.getCurrentItem();

                assert clicked != null;
                assert clicked.getItemMeta() != null;

                String rawName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

                Element picked = Element.getElement(rawName);

                new AbilityMenu(picked).show(player);
            });
            background.addItem(guiItem, x + 1, y + 1);

            x = x + 2;
            if (x >= paneWidth) {
                x = 0;
            }
        }
    }

    /**
     * Builds a list of ItemStacks for each element the player has, sorted by name.
     */
    private List<ItemStack> elementItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Element element : elements) {
            Material mat;
            switch (element.getName()) {
                case "Fire":  mat = Material.CAMPFIRE;     break;
                case "Water": mat = Material.WATER_BUCKET; break;
                case "Air":   mat = Material.WIND_CHARGE;  break;
                case "Earth": mat = Material.DIRT;         break;
                default: continue;
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(element.getColor() + element.getName());
                item.setItemMeta(meta);
            }
            items.add(item);
        }
        // sort alphabetically by display name
        items.sort(Comparator.comparing(i -> Objects.requireNonNull(i.getItemMeta()).getDisplayName()));
        return items;
    }
}
