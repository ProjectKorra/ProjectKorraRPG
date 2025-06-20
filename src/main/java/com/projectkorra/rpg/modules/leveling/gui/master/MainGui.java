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
import org.bukkit.inventory.meta.SkullMeta;

public class MainGui extends ChestGui {
    private static final int WIDTH = 9;
    private static final int HEIGHT = 3;

    private final StaticPane background;

    private final Player player;

    public MainGui(Player player) {
        super(HEIGHT, ChatUtil.color("Leveling"));
        this.player = player;

        setOnGlobalClick(event -> event.setCancelled(true));

        background = new StaticPane(0, 0, WIDTH, HEIGHT);
        background.fillWith(GuiItems.glassPaneItem().getItem());

        for (int y = 0; y < HEIGHT; y++) {
            background.addItem(GuiItems.twistedVinesItem(), 0, y);
            background.addItem(GuiItems.twistedVinesItem(), WIDTH - 1, y);
        }

        addPane(background);

        this.setupGui();
    }

    private void setupGui() {
        GuiItem shardButton = new GuiItem(amethystShardItem(), event -> {
            Player player = (Player) event.getWhoClicked();
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            if (bPlayer.getElements().isEmpty()) {
                player.sendMessage("You don't have any elements yet!");
                event.setCancelled(true);
                return;
            }

            if (bPlayer.getElements().size() == 1) {
                Element element = bPlayer.getElements().getFirst();
                event.setCancelled(true);
                new AbilityMenu(element).show(player);
            } else {
                event.setCancelled(true);
                new ElementPicker(player).show(player);
            }
        });

        background.addItem(shardButton, 2, 1);

        GuiItem headItem = new GuiItem(getHead(), event -> {
            event.setCancelled(true);
        });

        background.addItem(headItem, 4, 1);

        GuiItem starButton = new GuiItem(netherStarItem(), event -> {
                    event.setCancelled(true);
        });

        background.addItem(starButton, 6, 1);
    }

    private ItemStack amethystShardItem() {
        ItemStack amethystShard = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta amethystShardMeta = amethystShard.getItemMeta();
        assert amethystShardMeta != null;
        amethystShardMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Attributes");
        amethystShard.setItemMeta(amethystShardMeta);

        return amethystShard;
    }

    private ItemStack getHead() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        assert skull != null;
        skull.setDisplayName(player.getName());
        skull.setOwningPlayer(player);
        skull.setOwnerProfile(player.getPlayerProfile());
        item.setItemMeta(skull);
        return item;
    }

    private ItemStack netherStarItem() {
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
        ItemMeta netherStarMeta = netherStar.getItemMeta();
        assert netherStarMeta != null;
        netherStarMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.ITALIC + "Skilltree");
        netherStar.setItemMeta(netherStarMeta);
        return netherStar;
    }
}
