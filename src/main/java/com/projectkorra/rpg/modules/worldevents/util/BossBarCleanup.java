package com.projectkorra.rpg.modules.worldevents.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.Locale;

public class BossBarCleanup {
    private BossBarCleanup() {}

    public static void removeAllFor(Plugin plugin) {
        String ns = plugin.getName().toLowerCase(Locale.ROOT);
        Iterator< KeyedBossBar> it = Bukkit.getBossBars();

        while (it.hasNext()) {
            KeyedBossBar bar = it.next();
            NamespacedKey key = bar.getKey();
            if (ns.equals(key.getNamespace())) {
                Bukkit.removeBossBar(key);
            }
        }
    }
}
