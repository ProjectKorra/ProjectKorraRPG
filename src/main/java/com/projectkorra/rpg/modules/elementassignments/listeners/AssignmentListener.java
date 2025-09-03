package com.projectkorra.rpg.modules.elementassignments.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.ProjectKorraRPG;
import com.projectkorra.rpg.modules.elementassignments.manager.AssignmentManager;
import com.projectkorra.rpg.modules.randomavatar.manager.AvatarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class AssignmentListener implements Listener {
    private final AssignmentManager assignmentManager;
    private final AvatarManager avatarManager;

    public AssignmentListener(ProjectKorraRPG plugin) {
        this.assignmentManager = plugin.getModuleManager().getElementAssignmentsModule().getAssignmentManager();
        this.avatarManager = plugin.getModuleManager().getRandomAvatarModule().getAvatarManager();
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerJoin(BendingPlayerLoadEvent event) {
        if (event.getBendingPlayer().isOnline()) {
            BendingPlayer bPlayer = (BendingPlayer) event.getBendingPlayer();
            if (bPlayer.getElements().isEmpty() && !bPlayer.isPermaRemoved()) {
                assignmentManager.assignRandomGroup(bPlayer, false);
            }
        }
    }

    @EventHandler
    public void onBendingPlayerDeath(final PlayerDeathEvent event) {
        if (!avatarManager.isCurrentRPGAvatar(event.getEntity().getUniqueId())) {
            assignmentManager.assignRandomGroup(BendingPlayer.getBendingPlayer(event.getEntity()), true);
        }
    }
}
