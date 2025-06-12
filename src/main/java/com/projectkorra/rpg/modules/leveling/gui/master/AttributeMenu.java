package com.projectkorra.rpg.modules.leveling.gui.master;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.leveling.gui.util.GuiItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AttributeMenu extends ChestGui {
    private static final int WIDTH = 9;
    private static final int HEIGHT = 6;

    private final CoreAbility coreAbility;
    private final StaticPane background;

    public AttributeMenu(CoreAbility coreAbility) {
        super(HEIGHT, ChatUtil.color(coreAbility.getElement().getColor() + coreAbility.getName() + " Attributes"));
        this.coreAbility = coreAbility;
        this.background = new StaticPane(0, 0, WIDTH, HEIGHT);

        setOnGlobalClick(event -> event.setCancelled(true));

        addPane(background);

        drawBackground();
        populatedAttributes();
    }

    private void drawBackground() {
        GuiItem vines = GuiItems.twistedVinesItem();
        GuiItem glass = GuiItems.glassPaneItem();

        GuiItem backIcon = new GuiItem(new ItemStack(Material.BARRIER), event -> {
            event.setCancelled(true);
            new AbilityMenu(coreAbility.getElement()).show(event.getWhoClicked());
        });

        GuiItem beforeIcon = new GuiItem(new ItemStack(Material.ARROW), event -> {

        });

        GuiItem nextIcon = new GuiItem(new ItemStack(Material.ARROW), event -> {

        });

        for (int x = 1; x < WIDTH - 1; x++) {
            background.addItem(glass, x, 0);
            background.addItem(glass, x, HEIGHT - 1);
        }

        for (int y = 0; y < HEIGHT; y++) {
            background.addItem(vines, 0, y);
            background.addItem(vines, WIDTH - 1, y);
        }

        background.addItem(beforeIcon, 3, 5);
        background.addItem(backIcon, 4, 5);
        background.addItem(nextIcon, 5, 5);
    }

    private void populatedAttributes() {
        List<AttributeEntry> entries = new ArrayList<>();
        for (Field field : coreAbility.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Attribute.class)) continue;
            field.setAccessible(true);

            try {
                Object value = field.get(coreAbility);
                String key = field.getAnnotation(Attribute.class).value();
                entries.add(new AttributeEntry(key, value));
            } catch (IllegalAccessException ignored) {}
        }

        entries.sort(Comparator.comparing(e -> e.key));

        int x = 1, y = 1;
        int innerWidth = WIDTH - 2;
        int innerHeight = HEIGHT - 2;

        for (AttributeEntry entry : entries) {
            if (y > innerHeight) break;

            ItemStack icon = createIcon(entry.key, entry.value);
            GuiItem guiItem = new GuiItem(icon, event -> {
                event.setCancelled(true);
            });
            background.addItem(guiItem, x, y);

            x = x + 2;
            if (x > innerWidth) {
                x = 1;
                y = y + 2;
            }
        }
    }

    private ItemStack createIcon(String attributeName, Object value) {
        Material mat = switch (attributeName.toLowerCase()) {
            case "speed" -> Material.RABBIT_FOOT;
            case "range" -> Material.ENDER_PEARL;
            case "damage" -> Material.DIAMOND;
            case "duration" -> Material.SLIME_BALL;
            case "radius" -> Material.HONEYCOMB;
            case "knockback" -> Material.STICK;
            case "fireticks" -> Material.BLAZE_POWDER;
            case "cooldown" -> Material.SUGAR;
            default -> Material.PAPER;
        };

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // display attribute name & value
            meta.setDisplayName(capitalize(attributeName));
            meta.setLore(Collections.singletonList(ChatUtil.color("&7Value: &f" + value)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    private record AttributeEntry(String key, Object value) {}
}
