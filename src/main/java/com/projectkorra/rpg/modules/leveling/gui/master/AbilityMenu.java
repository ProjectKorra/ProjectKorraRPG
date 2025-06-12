package com.projectkorra.rpg.modules.leveling.gui.master;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.*;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.configuration.ConfigManager;
import com.projectkorra.rpg.modules.leveling.gui.util.GuiItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;
import java.util.TreeSet;

public class AbilityMenu extends ChestGui {
    private static final int WIDTH = 9;
    private static final int HEIGHT = 6;

    private final StaticPane outlinePane;
    private final StaticPane abilityPane;
    private final Element element;

    public AbilityMenu(Element element) {
        super(HEIGHT, ChatUtil.color("Abilities"));

        this.outlinePane = new StaticPane(0, 0, WIDTH, HEIGHT);
        this.abilityPane = new StaticPane(1, 1, WIDTH - 2, HEIGHT - 2);

        this.element = element;

        setOnGlobalClick(event -> event.setCancelled(true));

        addPane(outlinePane);
        addPane(abilityPane);

        outlinePane.setVisible(true);
        abilityPane.setVisible(true);

        setupGui();
    }

    private void setupGui() {
        GuiItem vines = GuiItems.twistedVinesItem();
        GuiItem glass = GuiItems.glassPaneItem();

        GuiItem backIcon = new GuiItem(new ItemStack(Material.BARRIER), event -> {
            event.setCancelled(true);
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer((Player) event.getWhoClicked());

            if (bPlayer.getElements().size() >= 2) {
                new ElementPicker((Player) event.getWhoClicked()).show(event.getWhoClicked());
            } else {
                new MainGui(bPlayer.getPlayer()).show(event.getWhoClicked());
            }
        });

        GuiItem beforeIcon = new GuiItem(new ItemStack(Material.ARROW), event -> {

        });

        GuiItem nextIcon = new GuiItem(new ItemStack(Material.ARROW), event -> {

        });

        // top and bottom rows
        for (int x = 1; x < WIDTH - 1; x++) {
            outlinePane.addItem(glass, x, 0);
            outlinePane.addItem(glass, x, HEIGHT - 1);
        }

        // left and right columns
        for (int y = 0; y < HEIGHT; y++) {
            outlinePane.addItem(vines, 0, y);
            outlinePane.addItem(vines, WIDTH - 1, y);
        }

        outlinePane.addItem(beforeIcon, 3, 5);
        outlinePane.addItem(backIcon, 4, 5);
        outlinePane.addItem(nextIcon, 5, 5);

        Set<String> sortedNames = new TreeSet<>();

        for (CoreAbility ability : CoreAbility.getAbilitiesByElement(element)) {
            if (!ability.isEnabled() || ability.isHiddenAbility()) continue;

            if (element == Element.FIRE && (ability instanceof LightningAbility || ability instanceof CombustionAbility))
                continue;

            if (element == Element.AIR && (ability instanceof FlightAbility || ability instanceof SpiritualAbility))
                continue;

            if (element == Element.EARTH && (ability instanceof MetalAbility || ability instanceof LavaAbility || ability instanceof SandAbility))
                continue;

            if (element == Element.WATER && (ability instanceof BloodAbility || ability instanceof IceAbility || ability instanceof HealingAbility || ability instanceof PlantAbility))
                continue;

            sortedNames.add(ability.getName());
        }

        for (String name : sortedNames) {
            ItemStack icon = abilityItem(name);
            abilityPane.addItem(new GuiItem(icon), 0, 0);
        }

        int x = 0, y = 0;
        for (String name : sortedNames) {
            if (y >= 5) break;

            ItemStack icon = abilityItem(name);

            GuiItem abilityItem = new GuiItem(icon, event -> {
                event.setCancelled(true);
                new AttributeMenu(CoreAbility.getAbility(name)).show(event.getWhoClicked());
            });

            abilityPane.addItem(abilityItem, x, y);

            x++;
            if (x >= abilityPane.getLength()) {
                x = 0;
                y++;
            }
        }
    }

    private ItemStack abilityItem(String name) {
        Material mat;

        switch (element.getName()) {
            case "Fire":
                mat = Material.getMaterial(ConfigManager.getDefaultFileConfig().getString("Modules.Leveling.Menu.FireIcon", Material.CAMPFIRE.toString()));
                break;
            case "Water":
                mat = Material.getMaterial(ConfigManager.getDefaultFileConfig().getString("Modules.Leveling.Menu.WaterIcon", Material.WATER_BUCKET.toString()));
                break;
            case "Air":
                mat = Material.getMaterial(ConfigManager.getDefaultFileConfig().getString("Modules.Leveling.Menu.AirIcon", Material.WIND_CHARGE.toString()));;
                break;
            case "Earth":
                mat = Material.getMaterial(ConfigManager.getDefaultFileConfig().getString("Modules.Leveling.Menu.EarthIcon", Material.DIRT.toString()));;
                break;
            default:
                mat = Material.AIR;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(element.getColor() + name);
        item.setItemMeta(meta);
        return item;
    }
}
