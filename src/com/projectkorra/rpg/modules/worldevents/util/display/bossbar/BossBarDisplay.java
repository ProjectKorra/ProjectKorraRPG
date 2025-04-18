package com.projectkorra.rpg.modules.worldevents.util.display.bossbar;

import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.rpg.modules.worldevents.WorldEvent;
import com.projectkorra.rpg.modules.worldevents.util.display.IWorldEventDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BossBarDisplay implements IWorldEventDisplay {

    @Override
    public void startDisplay(WorldEvent event) {
        event.setWorldEventBossBar(new WorldEventBossBar(ChatUtil.color(event.getName()), event.getBarColor(), event.getBarStyle()));

        Set<String> blacklistedNames = getBlacklistedWorldNames(event);

        for (Player player : Bukkit.getOnlinePlayers()) {
            String worldName = player.getWorld().getName();
            if (!blacklistedNames.contains(worldName)) {
                event.getWorldEventBossBar().getBossBar().addPlayer(player);
            }
        }
    }

    @Override
    public void updateDisplay(WorldEvent event, double progress) {
        if (event.getWorldEventBossBar() != null && event.getWorldEventBossBar().getBossBar() != null) {
            event.getWorldEventBossBar().getBossBar().setProgress(progress);
        }
    }

    @Override
    public void stopDisplay(WorldEvent event) {
        if (event.getWorldEventBossBar() != null && event.getWorldEventBossBar().getBossBar() != null) {
            event.getWorldEventBossBar().getBossBar().removeAll();
        }
    }

    /**
     * Helper method to extract a set of valid blacklisted world names from the event.
     */
    private Set<String> getBlacklistedWorldNames(WorldEvent event) {
        Set<String> names = new HashSet<>();
        List<org.bukkit.World> blWorlds = event.getBlacklistedWorlds();
        if (blWorlds != null) {
            for (org.bukkit.World world : blWorlds) {
                if (world != null) {
                    names.add(world.getName());
                }
            }
        }
        return names;
    }
}
