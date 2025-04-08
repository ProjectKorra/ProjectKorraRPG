package com.projectkorra.rpg.avatar;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.rpg.ProjectKorraRPG;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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
}
