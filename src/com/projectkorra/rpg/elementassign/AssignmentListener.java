package com.projectkorra.rpg.elementassign;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.RPGMethods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AssignmentListener implements Listener {

    public AssignmentListener() {
        super();
    }

    @EventHandler
    public void onPlayerJoin(BendingPlayerLoadEvent event) {
        if (event.getBendingPlayer().isOnline()) {
            BendingPlayer bPlayer = (BendingPlayer) event.getBendingPlayer();
            if (bPlayer.getElements().isEmpty() && !bPlayer.isPermaRemoved()) {
                ProjectKorraRPG.plugin.getAssignmentManager().assignRandomGroup(bPlayer, false);
            }
        }
    }

//    @EventHandler
//    public void onBendingPlayerDeath(PlayerDeathEvent event) {
//        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getEntity());
//        if (bPlayer != null) {
//            if (ProjectKorraRPG.plugin.getAvatarManager().isEnabled()) {
//                if (ProjectKorraRPG.plugin.getAvatarManager().isCurrentRPGAvatar(bPlayer.getUUID())) {
//                    return;
//                }
//            }
//            ProjectKorraRPG.plugin.getAssignmentManager().assignRandomGroup(bPlayer, true);
//        }
//    }

}
