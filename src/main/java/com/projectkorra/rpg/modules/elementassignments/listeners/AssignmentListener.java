package com.projectkorra.rpg.modules.elementassignments.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.event.BendingPlayerLoadEvent;
import com.projectkorra.rpg.ProjectKorraRPG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AssignmentListener implements Listener {

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerJoin(BendingPlayerLoadEvent event) {
        if (event.getBendingPlayer().isOnline()) {
            BendingPlayer bPlayer = (BendingPlayer) event.getBendingPlayer();
            if (bPlayer.getElements().isEmpty() && !bPlayer.isPermaRemoved()) {
                ProjectKorraRPG.getPlugin().getModuleManager().getElementAssignmentsModule().getAssignmentManager().assignRandomGroup(bPlayer, false);
            }
        }
    }

}
