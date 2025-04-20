package com.projectkorra.rpg.modules.randomavatar.listeners;

import com.projectkorra.rpg.ProjectKorraRPG;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AvatarListener implements Listener {
    public AvatarListener() {
        super();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
        ProjectKorraRPG.getPlugin().getModuleManager().getRandomAvatarModule().getAvatarManager().recentPlayers.remove(player);
        ProjectKorraRPG.getPlugin().getModuleManager().getRandomAvatarModule().getAvatarManager().recentPlayers.add(player);
        ProjectKorraRPG.getPlugin().getModuleManager().getRandomAvatarModule().getAvatarManager().checkAvatars();
    }
}
