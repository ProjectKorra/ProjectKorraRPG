package com.projectkorra.rpg.modules.randomavatar;

import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.ProjectKorraRPG;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AvatarListener implements Listener {
    public AvatarListener() {
        super();
    }
//    @EventHandler
//    public void onBendingPlayerDeath(PlayerDeathEvent event) {
//        if (ProjectKorraRPG.plugin.getAvatarManager().isEnabled()) {
//            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getEntity());
//            if (bPlayer != null) {
//                if (ProjectKorraRPG.plugin.getAvatarManager().handleAvatarDeath(bPlayer)) {
//                    bPlayer.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You have been lost Avatar");
//                    ProjectKorraRPG.log.info(bPlayer.getName() + " is no longer the Avatar.");
//                }
//            }
//        }
//    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
        ProjectKorraRPG.plugin.getAvatarManager().recentPlayers.remove(player);
        ProjectKorraRPG.plugin.getAvatarManager().recentPlayers.add(player);
        ProjectKorraRPG.plugin.getAvatarManager().checkAvatars();
    }


}
